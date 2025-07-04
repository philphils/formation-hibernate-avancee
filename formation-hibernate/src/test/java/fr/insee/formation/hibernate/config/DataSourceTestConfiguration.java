package fr.insee.formation.hibernate.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.p6spy.engine.spy.P6DataSource;

import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/**
 * Cette classe permet de wrappé (enrobé) la {@link DataSource} pour lui ajouter
 * d'autres fonctionnalité pour les tests seulement (sinon dégradation des
 * performances)
 * 
 * @author SYV27O
 *
 */

@TestConfiguration
@Slf4j
public class DataSourceTestConfiguration {

    @Value("${activate.p6spy:false}")
    private boolean activateP6spy;

    @Value("${activate.datasource-proxy:false}")
    private boolean activateDatasourceProxy;

    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource baseDataSource(DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Primary
    public DataSource wrappedDataSource(@Qualifier("baseDataSource") DataSource baseDataSource) {

        DataSource wrapped = baseDataSource;

        try {
            log.info("URL: {}", wrapped.getConnection().getMetaData().getURL());
        } catch (SQLException e) {
            log.warn("Can't get connection URL", e);
        }

        if (activateDatasourceProxy) {
            wrapped = ProxyDataSourceBuilder.create(wrapped)
                    .name("wrappedDataSource")
                    .logQueryBySlf4j(SLF4JLogLevel.DEBUG)
                    .countQuery()
                    .proxyResultSet()
                    .multiline()
                    .build();
        }

        if (activateP6spy) {
            wrapped = new P6DataSource(wrapped);
        }

        return wrapped;
    }

}