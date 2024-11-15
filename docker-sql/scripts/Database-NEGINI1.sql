USE [master]
GO
/****** Object:  Database [NEGINI1]    Script Date: 14-07-2024 14:39:17 ******/
CREATE DATABASE [NEGINI1]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'NEGINI1_Data', FILENAME = N'/var/opt/mssql/data/NEGINI1_Data.mdf' , SIZE = 27392000KB , MAXSIZE = UNLIMITED, FILEGROWTH = 512000KB )
 LOG ON 
( NAME = N'NEGINI1_Log', FILENAME = N'/var/opt/mssql/data/NEGINI1_log.ldf' , SIZE = 181184KB , MAXSIZE = UNLIMITED, FILEGROWTH = 10%)
GO
ALTER DATABASE [NEGINI1] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [NEGINI1].[dbo].[sp_fulltext_database] @action = 'disable'
end
GO
ALTER DATABASE [NEGINI1] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [NEGINI1] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [NEGINI1] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [NEGINI1] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [NEGINI1] SET ARITHABORT OFF 
GO
ALTER DATABASE [NEGINI1] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [NEGINI1] SET AUTO_CREATE_STATISTICS ON 
GO
ALTER DATABASE [NEGINI1] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [NEGINI1] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [NEGINI1] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [NEGINI1] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [NEGINI1] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [NEGINI1] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [NEGINI1] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [NEGINI1] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [NEGINI1] SET  DISABLE_BROKER 
GO
ALTER DATABASE [NEGINI1] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [NEGINI1] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [NEGINI1] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [NEGINI1] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [NEGINI1] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [NEGINI1] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [NEGINI1] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [NEGINI1] SET RECOVERY FULL 
GO
ALTER DATABASE [NEGINI1] SET  MULTI_USER 
GO
ALTER DATABASE [NEGINI1] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [NEGINI1] SET DB_CHAINING OFF 
GO
ALTER DATABASE [NEGINI1] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [NEGINI1] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
EXEC sys.sp_db_vardecimal_storage_format N'NEGINI1', N'ON'
GO