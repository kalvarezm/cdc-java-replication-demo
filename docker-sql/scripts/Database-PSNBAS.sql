USE [master]
GO
/****** Object:  Database [PSNBAS]    Script Date: 14-07-2024 14:39:17 ******/
CREATE DATABASE [PSNBAS]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'PSNBAS_Data', FILENAME = N'/var/opt/mssql/data/PSNBAS_Data.mdf' , SIZE = 27392000KB , MAXSIZE = UNLIMITED, FILEGROWTH = 512000KB )
 LOG ON 
( NAME = N'PSNBAS_Log', FILENAME = N'/var/opt/mssql/data/PSNBAS_log.ldf' , SIZE = 181184KB , MAXSIZE = UNLIMITED, FILEGROWTH = 10%)
GO
ALTER DATABASE [PSNBAS] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [PSNBAS].[dbo].[sp_fulltext_database] @action = 'disable'
end
GO
ALTER DATABASE [PSNBAS] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [PSNBAS] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [PSNBAS] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [PSNBAS] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [PSNBAS] SET ARITHABORT OFF 
GO
ALTER DATABASE [PSNBAS] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [PSNBAS] SET AUTO_CREATE_STATISTICS ON 
GO
ALTER DATABASE [PSNBAS] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [PSNBAS] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [PSNBAS] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [PSNBAS] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [PSNBAS] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [PSNBAS] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [PSNBAS] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [PSNBAS] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [PSNBAS] SET  DISABLE_BROKER 
GO
ALTER DATABASE [PSNBAS] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [PSNBAS] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [PSNBAS] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [PSNBAS] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [PSNBAS] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [PSNBAS] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [PSNBAS] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [PSNBAS] SET RECOVERY FULL 
GO
ALTER DATABASE [PSNBAS] SET  MULTI_USER 
GO
ALTER DATABASE [PSNBAS] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [PSNBAS] SET DB_CHAINING OFF 
GO
ALTER DATABASE [PSNBAS] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [PSNBAS] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
EXEC sys.sp_db_vardecimal_storage_format N'PSNBAS', N'ON'
GO