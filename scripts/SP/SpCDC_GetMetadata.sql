--#Begin-Doc
-- Autor        :   Kevin Alvarez Mouravskaia nuam.  --KAM--
-- Fecha        :   Agosto - 2024
-- Descripcion  :   
--#Fin-Doc

CREATE OR ALTER PROCEDURE [dbo].[SpCDC_GetMetadata]
(
	@databaseName VARCHAR(100),
	@schemaName VARCHAR(50),
	@tableName VARCHAR(100)
)
AS
BEGIN  
	-- Stop counts
	SET NOCOUNT ON;

    -- Get table definition
	SELECT
		col.ORDINAL_POSITION,
		col.TABLE_NAME,
		col.COLUMN_NAME,
		col.DATA_TYPE,
		co.CONSTRAINT_TYPE,
		CASE 
			WHEN co.CONSTRAINT_TYPE = 'PRIMARY KEY' THEN 1 ELSE 0
		END AS IS_PRIMARY_KEY,
		CASE 
			WHEN col.IS_NULLABLE = 'NO' THEN 0 ELSE 1
		END AS IS_NULLABLE,
		CONCAT(
			col.COLUMN_NAME, 
			' ', 
			col.DATA_TYPE,
			CASE
				WHEN col.DATA_TYPE IN ('char', 'varchar', 'nvarchar', 'nchar', 'varbinary', 'binary') 
					THEN CONCAT(
						'(', 
						(CASE 
							WHEN col.CHARACTER_MAXIMUM_LENGTH = -1 THEN 'max'
							ELSE CAST(col.CHARACTER_MAXIMUM_LENGTH AS varchar)
						END
						), 
						')')
				WHEN col.DATA_TYPE IN ('decimal', 'numeric') 
					THEN CONCAT('(', col.NUMERIC_PRECISION , ',', col.NUMERIC_SCALE, ')')
				WHEN col.DATA_TYPE IN ('int', 'datetime', 'text', 'bigint', 'bit', 
						'money', 'tinyint', 'smalldatetime', 'timestamp', 'image', 
						'smallint', 'real', 'float') 
					THEN ''
				ELSE ''
			END,
			' ', 
			CASE
				WHEN col.IS_NULLABLE = 'YES' THEN 'NULL'
				ELSE 'NOT NULL'
			END
		) AS COLUMN_DEFINITION,
		col.CHARACTER_MAXIMUM_LENGTH,
		col.NUMERIC_PRECISION,
		col.NUMERIC_SCALE
	FROM INFORMATION_SCHEMA.COLUMNS col
	LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE cu
		ON col.TABLE_NAME = cu.TABLE_NAME AND col.COLUMN_NAME = cu.COLUMN_NAME
	LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS co
		ON co.CONSTRAINT_NAME = cu.CONSTRAINT_NAME 
	WHERE
		col.TABLE_CATALOG = @databaseName
		AND col.TABLE_SCHEMA = @schemaName
		AND col.TABLE_NAME = @tableName
	ORDER BY col.TABLE_NAME;
	 
END