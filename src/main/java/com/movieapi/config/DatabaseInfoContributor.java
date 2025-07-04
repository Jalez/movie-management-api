package com.movieapi.config;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Component
public class DatabaseInfoContributor implements InfoContributor {

    private final DataSource dataSource;

    public DatabaseInfoContributor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void contribute(Info.Builder builder) {
        // Add database information
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            builder.withDetail("database", 
                new DatabaseInfo(
                    metaData.getDatabaseProductName(),
                    metaData.getDatabaseProductVersion(),
                    metaData.getDriverName(),
                    metaData.getDriverVersion()
                )
            );
        } catch (SQLException e) {
            builder.withDetail("database", 
                new DatabaseInfo("Unknown", "Unknown", "Unknown", "Unknown")
            );
        }
        
        // Add Spring Boot framework version dynamically
        builder.withDetail("framework", 
            new FrameworkInfo("Spring Boot", SpringBootVersion.getVersion())
        );
        
        // Add Java runtime information dynamically
        builder.withDetail("runtime", 
            new RuntimeInfo(
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                System.getProperty("java.vm.name")
            )
        );
    }

    public static class DatabaseInfo {
        private final String productName;
        private final String productVersion;
        private final String driverName;
        private final String driverVersion;

        public DatabaseInfo(String productName, String productVersion, String driverName, String driverVersion) {
            this.productName = productName;
            this.productVersion = productVersion;
            this.driverName = driverName;
            this.driverVersion = driverVersion;
        }

        public String getProductName() { return productName; }
        public String getProductVersion() { return productVersion; }
        public String getDriverName() { return driverName; }
        public String getDriverVersion() { return driverVersion; }
    }
    
    public static class FrameworkInfo {
        private final String name;
        private final String version;

        public FrameworkInfo(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName() { return name; }
        public String getVersion() { return version; }
    }
    
    public static class RuntimeInfo {
        private final String javaVersion;
        private final String javaVendor;
        private final String javaVm;

        public RuntimeInfo(String javaVersion, String javaVendor, String javaVm) {
            this.javaVersion = javaVersion;
            this.javaVendor = javaVendor;
            this.javaVm = javaVm;
        }

        public String getJavaVersion() { return javaVersion; }
        public String getJavaVendor() { return javaVendor; }
        public String getJavaVm() { return javaVm; }
    }
}
