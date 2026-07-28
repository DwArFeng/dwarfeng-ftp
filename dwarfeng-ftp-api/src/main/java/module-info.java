module com.dwarfeng.ftp.api {

    requires transitive com.dwarfeng.ftp.core;
    requires transitive com.dwarfeng.springtelqos.core;
    requires jakarta.annotation;
    requires org.apache.commons.cli;
    requires org.apache.commons.lang3;

    exports com.dwarfeng.ftp.api.integration.springtelqos;
}
