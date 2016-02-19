package com.blackbox.cvrt.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Jerrick Pua
 */
public class RestUtils {
    public static RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
        formConverter.setCharset( Charset.forName( "UTF8" ) );
        restTemplate.getMessageConverters().add( formConverter );
        restTemplate.getMessageConverters().add( new MappingJackson2HttpMessageConverter() );
        restTemplate.setErrorHandler( new ResponseErrorHandler() {
            @Override
            public boolean hasError( ClientHttpResponse response ) throws IOException {
                HttpStatus status = response.getStatusCode();
                if( HttpStatus.CREATED.equals( status ) || HttpStatus.OK.equals( status ) ) {
                    return false;
                }
                return true;
            }

            @Override
            public void handleError( ClientHttpResponse response ) throws IOException {
                throw new HttpClientErrorException( response.getStatusCode() );
            }
        } );
        return restTemplate;
    }
}
