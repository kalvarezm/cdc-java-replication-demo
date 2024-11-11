--#Begin-Doc
-- Autor        :   Kevin Alvarez Mouravskaia nuam.  --KAM--
-- Fecha        :   Agosto - 2024
-- Descripcion  :   
--#Fin-Doc

CREATE OR ALTER PROCEDURE [dbo].[SpCDC_BeginTransaction]
(
	@databaseName VARCHAR(100),
	@schemaName VARCHAR(50),
	@sourceTable VARCHAR(100),
	@showLastCheckPoint INT
)
AS
BEGIN
	-- Stop counts
	SET NOCOUNT ON;

	-- Tables
    DECLARE @tableSchemaName VARCHAR(150);
	SET @tableSchemaName = @schemaName + '_' + @sourceTable;

	-- Dynamic query
	DECLARE @dynSqlFnCdGetMinLsn NVARCHAR(1000);
	DECLARE @dynSqlFnCdcGetAllChangesTop1Count NVARCHAR(1000);

	-- Checkpoints
	DECLARE @idCheckPoint INT;
	DECLARE @minLsnCheckPoint BINARY(10);
	DECLARE @maxLsnCheckPoint BINARY(10);
	DECLARE @lastLsn BINARY(10);
	DECLARE @lastSeqVal BINARY(10);

    -- New CDC transactions
	DECLARE @minLsnNewTrn BINARY(10);
	DECLARE @maxLsnNewTrn BINARY(10);
	DECLARE @nextLsnNewTrn BINARY(10);
	DECLARE @countNewTrn INT;

	-- Status
	DECLARE @status_ok INT
	SET @status_ok = 1

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
	SET @dynSqlFnCdGetMinLsn =
		'SELECT @minLsnNewTrn = sys.fn_cdc_get_min_lsn (@tableSchemaName);';
	SET @dynSqlFnCdcGetAllChangesTop1Count =
		'SELECT @countNewTrn = COUNT(*) FROM (SELECT TOP 1 * FROM cdc.fn_cdc_get_all_changes_'
			+ @tableSchemaName + '(@minLsn, @maxLsn, ''all'')) cdc;';

	-- Output
	CREATE TABLE #Result (
		MIN_LSN BINARY(10),
		MAX_LSN BINARY(10),
		LAST_LSN BINARY(10),
   		LAST_SEQVAL BINARY(10)
	)

	-- Get last LSN record from CDC_CONTROL
	SELECT TOP 1
		@idCheckPoint = ID,
		@minLsnCheckPoint = MIN_LSN,
		@maxLsnCheckPoint = MAX_LSN,
		@lastLsn = LAST_LSN,
		@lastSeqVal = LAST_SEQVAL
	FROM
		SYNCRO_TRANSACTION_CONTROL
	WHERE
		DATABASE_TARGET = @databaseName
		AND SCHEMA_TARGET = @schemaName
		AND SOURCE_TABLE = @sourceTable
		AND STATUS = @status_ok
	ORDER BY
		ID DESC;

	-- If you want last change
	IF (@showLastCheckPoint = 1) BEGIN
		INSERT INTO
			#Result (MIN_LSN, MAX_LSN, LAST_LSN, LAST_SEQVAL)
		VALUES (@minLsnCheckPoint, @maxLsnCheckPoint, @lastLsn, @lastSeqVal);
	END
	-- If you want new changes
	ELSE IF (@showLastCheckPoint = 0) BEGIN

		-- Get min LSN from CDC when there is no checkpoint
		IF (@idCheckPoint IS NULL) BEGIN
			EXEC sp_executesql @dynSqlFnCdGetMinLsn,
				N'@tableSchemaName VARCHAR(100), @minLsnNewTrn BINARY(10) OUTPUT',
				@tableSchemaName = @tableSchemaName,
				@minLsnNewTrn = @minLsnNewTrn OUTPUT;
		END
		ELSE BEGIN
			SET @minLsnNewTrn = @maxLsnCheckPoint;
		END

		-- Get max LSN from CDC
		SET @maxLsnNewTrn = (SELECT sys.fn_cdc_get_max_lsn());
		-- Get next increment
		SET @nextLsnNewTrn = (SELECT sys.fn_cdc_increment_lsn(@minLsnNewTrn))

		-- Validate if min > max
		IF(@maxLsnNewTrn >= @nextLsnNewTrn) BEGIN

			-- Set Next LSN
			SET @minLsnNewTrn = @nextLsnNewTrn;

			-- Get changes
			EXEC sp_executesql @dynSqlFnCdcGetAllChangesTop1Count,
					N'@minLsn BINARY(10), @maxLsn BINARY(10), @countNewTrn INT OUTPUT',
					@minLsn = @minLsnNewTrn,
					@maxLsn = @maxLsnNewTrn,
					@countNewTrn = @countNewTrn OUTPUT;

			-- If you want new changes
			IF (@countNewTrn > 0) BEGIN
				INSERT INTO
					#Result (MIN_LSN, MAX_LSN, LAST_LSN, LAST_SEQVAL)
				VALUES (@minLsnNewTrn, @maxLsnNewTrn, NULL, NULL);
			END

		END

	END

	-- Show result
	SELECT * FROM #Result;
	-- Delete temporal table
	DROP TABLE #Result
	 
END