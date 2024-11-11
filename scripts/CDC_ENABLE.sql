------------------------
-- ENABLE CDC
------------------------
-- Enable
sys.sp_cdc_enable_db;
GO
-- Disable
--sys.sp_cdc_disable_db;
--GO

------------------------
-- ENABLE CDC TABLES
------------------------
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBACMI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCFMI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBFUMI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBIFTR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBORTR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBOTPTAS', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRVMI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRVPCDI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRVTR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRVTRBO', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRVTRMT', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBTIB', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBTOM', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBUTPARA', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBALBENC', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBALCIER', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBAVI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBBOMTPR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBSILOGC', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCORPTA', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBIEPF', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBIEPFDI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBIEREHO', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBIFIHPR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBIFTMPR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBMOMI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBPRAJHI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFMI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTMPR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTMTA', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBACFVC', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBACINDI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBACRENT', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBACVCLO', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBALGFON', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBALRIES', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBALRITN', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBASACC', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBASBAL', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBASDIR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBASPAR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBASSER', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBASSOC', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCLAGDT', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCLCOMP', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCLCTR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCLOPOT', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCLREDT', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCUDCO', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCUDET', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCUOFM', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCUVCDE', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBIFTMCR', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBINVC', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFIHGN', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFIPRC', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFRTMD', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTEBE', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTIRM', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTMCL', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTMCS', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBTRBNCH', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBACCADI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBACCDAT', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCOCUEN', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCUMOVI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCUUTI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFSE', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFTD', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBSOCNRG', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBTPCUEN', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBTPPART', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCLCICA', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBCLOPER', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBINDICE', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBINPESO', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBRFINDI', @role_name=NULL, @supports_net_changes=1;
GO
sys.sp_cdc_enable_table @source_schema='dbo', @source_name='TBUTPTOS', @role_name=NULL, @supports_net_changes=1;
GO


