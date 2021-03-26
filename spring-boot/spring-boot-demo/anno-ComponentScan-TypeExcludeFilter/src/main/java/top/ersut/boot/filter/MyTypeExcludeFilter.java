package top.ersut.boot.filter;

import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

public class MyTypeExcludeFilter extends TypeExcludeFilter {

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory){

        if ( metadataReader.getClassMetadata().getClassName().equals("top.ersut.boot.filter.pojo.Student") ) {
            return true;
        }

        return false;

    }

}
