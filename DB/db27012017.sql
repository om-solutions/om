USE [danpac]
GO
/****** Object:  Table [dbo].[meterdata]    Script Date: 01/27/2017 20:02:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[meterdata](
	[PTemp] [real] NULL,
	[PPressure] [real] NULL,
	[MTemp] [real] NULL,
	[MPressure] [real] NULL,
	[Density] [real] NULL,
	[Flowrate] [real] NULL,
	[Current_K_Factor] [real] NULL,
	[Proved_K_Factor] [real] NULL,
	[Predicted_K_Factor] [real] NULL,
	[timestamp] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[members]    Script Date: 01/27/2017 20:02:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[members](
	[uname] [varchar](50) NULL,
	[pass] [varchar](50) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[masterData]    Script Date: 01/27/2017 20:02:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[masterData](
	[dt] [datetime] NULL,
	[dbName] [varchar](max) NULL,
	[tableName] [varchar](max) NULL,
	[columnsName] [varchar](max) NULL,
	[chartDT] [varchar](max) NULL,
	[url] [varchar](max) NULL,
	[dbInstanceName] [varchar](max) NULL,
	[userName] [varchar](max) NULL,
	[password] [varchar](max) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
