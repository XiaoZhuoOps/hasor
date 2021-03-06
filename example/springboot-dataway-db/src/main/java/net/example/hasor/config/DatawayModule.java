package net.example.hasor.config;
import net.example.hasor.udf.MyNameUdf;
import net.hasor.core.ApiBinder;
import net.hasor.core.DimModule;
import net.hasor.dataql.DimUdf;
import net.hasor.dataql.DimUdfSource;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.spring.SpringModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Objects;

@DimModule
@Component
public class DatawayModule implements SpringModule {
    @Resource(name = "metadataDs")
    private DataSource metadataDs = null;
    @Resource(name = "dataDs1")
    private DataSource dataDs1    = null;
    @Resource(name = "dataDs2")
    private DataSource dataDs2    = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .check
        Objects.requireNonNull(this.metadataDs, "metadataDs is null");
        Objects.requireNonNull(this.dataDs1, "dataDs1 is null");
        Objects.requireNonNull(this.dataDs2, "dataDs2 is null");
        // .DataSource form Spring boot into Hasor
        apiBinder.installModule(new JdbcModule(Level.Full, this.metadataDs));
        apiBinder.installModule(new JdbcModule(Level.Full, "ds1", this.dataDs1));
        apiBinder.installModule(new JdbcModule(Level.Full, "ds2", this.dataDs2));
        // .custom DataQL
        QueryApiBinder queryBinder = apiBinder.tryCast(QueryApiBinder.class);
        queryBinder.loadUdf(apiBinder.findClass(DimUdf.class), aClass -> true, springTypeSupplier(apiBinder));
        queryBinder.loadUdfSource(apiBinder.findClass(DimUdfSource.class), aClass -> true, springTypeSupplier(apiBinder));
    }
}