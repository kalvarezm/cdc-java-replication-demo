--#Begin-Doc
-- Autor        :   Kevin Alvarez Mouravskaia nuam.  --KAM--
-- Fecha        :   Agosto - 2024
-- Descripcion  :   
--#Fin-Doc

CREATE OR ALTER PROCEDURE [dbo].[SpCDC_ErrorTransaction]
(
	@databaseName VARCHAR(100),
	@schemaName VARCHAR(50),
	@sourceTable VARCHAR(100),
	@targetTable VARCHAR(100),
	@charMinLsn CHAR(20),
	@charMaxLsn CHAR(20),
	@charLastLsn CHAR(20),
	@charLastSeqVal CHAR(20),
	@countTrn INT,
	@failedTrn INT,
	@message VARCHAR(1000),
	@codeSqlState VARCHAR(10),
	@errorCode INT
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
	-- Checkpoints
	DECLARE @idCheckPoint INT;
	DECLARE @minLsnCheckPoint BINARY(10);
	DECLARE @maxLsnCheckPoint BINARY(10);
	-- Variables
	DECLARE @registerDate DATETIME;

	-- Tables
	SET @tableSchemaName = @schemaName + '_' + @sourceTable;
	-- Convert params
	SET @minLsn = CONVERT(BINARY(10), @charMinLsn, 2);
	SET @maxLsn = CONVERT(BINARY(10), @charMaxLsn, 2);
	SET @lastLsn = CONVERT(BINARY(10), @charLastLsn, 2);
	SET @lastSeqVal = CONVERT(BINARY(10), @charLastSeqVal, 2);
	-- Variables
	SET @registerDate = GETDATE();

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
			
	INSERT SYNCRO_TRANSACTION_ERROR(
			DATABASE_TARGET,
			SCHEMA_TARGET,
			SOURCE_TABLE,
			TARGET_TABLE, 
			MIN_LSN,
			MAX_LSN,
			LAST_LSN,
			LAST_SEQVAL,
			NUM_TRANSACTIONS,
			REGISTER_DATE,
			FAILED_TRANSACTIONS, 
			MESSAGE,
			CODE_SQL_STATE,
			ERROR_CODE
		)
		VALUES (
			@databaseName,
			@schemaName,
			@sourceTable,
			@targetTable,
			@minLsn, 
			@maxLsn,
			@lastLsn,
			@lastSeqVal,
			@countTrn,
			@registerDate,
			@failedTrn,
			@message, 
			@codeSqlState,
			@errorCode
		);
	 
END