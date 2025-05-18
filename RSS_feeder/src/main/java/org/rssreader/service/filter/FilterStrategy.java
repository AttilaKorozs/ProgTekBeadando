package org.rssreader.service.filter;
import java.util.List;

public interface FilterStrategy<T>{

        List<T> filter(List<T> items);

}
