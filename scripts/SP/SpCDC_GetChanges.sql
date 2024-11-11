--#Begin-Doc
-- Autor        :   Kevin Alvarez Mouravskaia nuam.  --KAM--
-- Fecha        :   Agosto - 2024
-- Descripcion  :
-- Parametros   :
-- 	* batchSize: cantidad de resultados a devolver. Usar -1 para todos los resultados
-- 	* charMinLsn: LSN inicial obtenido desde CDC
-- 	* charMaxLsn: LSN final obtenido desde CDC
-- 	* charLastLsn: Ultimo LSN utilizado. Usar charMinLsn para primeros resultados
-- 	* charLastSeqVal: Ultimo SeqVal utilizado. Usar NULL para primeros resultados.
-- Examples     :
--  * EXEC dbo.SpCDC_GetChanges 'NEGBAS', 'dbo', 'TBCLOPER', -1, '000000D00002284000A1', '000000D000039B38009F', '000000D00002284000A1', NULL; --all results
--  * EXEC dbo.SpCDC_GetChanges 'NEGBAS', 'dbo', 'TBCLOPER', 200, '000000D00002284000A1', '000000D000039B38009F', '000000D00002284000A1', NULL; --first 200 results
--  * EXEC dbo.SpCDC_GetChanges 'NEGBAS', 'dbo', 'TBCLOPER', 100, '000000D00002284000A1', '000000D000039B38009F', '000000D00002284000A1', '000000D0000301F8000B'; --next 100 results
--#Fin-Doc

CREATE OR ALTER PROCEDURE [dbo].[SpCDC_GetChanges]
(
	@databaseName VARCHAR(100),
	@schemaName VARCHAR(50),
	@sourceTable VARCHAR(100),
	@batchSize INT,
	@charMinLsn CHAR(20),
	@charMaxLsn CHAR(20),
	@charLastLsn CHAR(20),
	@charLastSeqVal CHAR(20)
)
AS
BEGIN
	-- Stop counts
	SET NOCOUNT ON;

    -- Params
	DECLARE @minLsn BINARY(10);
	DECLARE @maxLsn BINARY(10);
	DECLARE @lastLsn BINARY(10);
	DECLARE @lastSeqVal BINARY(10);
	-- Tables
    DECLARE @tableSchemaName VARCHAR(150);
	-- Dynamic query
	DECLARE @dynSqlFnCdcGetChanges NVARCHAR(1000);
    -- Constants
    DECLARE @ALL_RESULTS INT;

 	-- Table schema name
	SET @tableSchemaName = @schemaName + '_' + @sourceTable;
	-- Convert params
	SET @minLsn = CONVERT(BINARY(10), @charMinLsn, 2);
	SET @maxLsn = CONVERT(BINARY(10), @charMaxLsn, 2);
	SET @lastLsn = CONVERT(BINARY(10), @charLastLsn, 2);
	SET @lastSeqVal = CONVERT(BINARY(10), @charLastSeqVal, 2);
    -- Constants
    SET @ALL_RESULTS = -1;

	-- Check if the table exists
	IF NOT EXISTS
	(
		SELECT
			*
		FROM
			sys.tables
		WHERE
			OBJECT_SCHEMA_NAME(object_id) = @schemaName
		    AND name = @sourceTable
		    AND is_tracked_by_cdc = 1
	)
	BEGIN
		RAISERROR('TABLE DOES NOT EXISTS OR IT IS NOT TRACKED BY CDC: %s', 16, 1, @tableSchemaName)
	END

	-- Dynamic query
	-- All results
	IF (@batchSize = @ALL_RESULTS) BEGIN
		SET @dynSqlFnCdcGetChanges =
			'SELECT * FROM cdc.fn_cdc_get_all_changes_'
				+ @tableSchemaName + '(@minLsn, @maxLsn, ''all'') '
			    + 'WHERE __$operation IN (1, 2, 4)'; -- We are not including Updated row before the change (3)
	END
	ELSE BEGIN
		-- First page
		IF (@lastSeqVal IS NULL) BEGIN
			SET @dynSqlFnCdcGetChanges =
				'SELECT TOP ' + CAST(@batchSize AS varchar)
					+ ' * FROM cdc.fn_cdc_get_all_changes_'
					+ @tableSchemaName + '(@minLsn, @maxLsn, ''all'') '
					+ 'WHERE __$operation IN (1, 2, 4)'; -- We are not including Updated row before the change (3)
		END
		-- Next pages
		ELSE BEGIN
			SET @dynSqlFnCdcGetChanges =
				'SELECT TOP ' + CAST(@batchSize AS varchar)
					+ ' * FROM cdc.fn_cdc_get_all_changes_'
					+ @tableSchemaName + '(@minLsn, @maxLsn, ''all'') '
				    + 'WHERE __$start_lsn >= @lastLsn AND __$seqval > @lastSeqVal'
				    + ' AND __$operation IN (1, 2, 4)'; -- We are not including Updated row before the change (3);
		END

	END

	-- Get changes
	EXEC sp_executesql @dynSqlFnCdcGetChanges,
		N'@minLsn BINARY(10), @maxLsn BINARY(10),  @lastLsn BINARY(10),  @lastSeqVal BINARY(10)',
		@minLsn = @minLsn,
		@maxLsn = @maxLsn,
		@lastLsn = @lastLsn,
		@lastSeqVal = @lastSeqVal;

END