package br.com.ifood.ifoodconnection.config;

import br.com.ifood.ifoodconnection.service.RestaurantService;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import java.util.Collection;
import java.util.Objects;

public class CustomCacheResolver implements CacheResolver {

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {

        if (context.getTarget().getClass() == RestaurantService.class) {
            if (context.getMethod().getName().equalsIgnoreCase("findByIds")) {

            }
        }
        context.getArgs();

        return null;
    }
}
