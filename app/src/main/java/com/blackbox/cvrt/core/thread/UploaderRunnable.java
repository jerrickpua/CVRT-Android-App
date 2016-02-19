package com.blackbox.cvrt.core.thread;

import android.location.Location;

import com.blackbox.cvrt.context.ApplicationContext;
import com.blackbox.cvrt.core.model.MP3Record;
import com.blackbox.cvrt.core.rest.model.APIResponse;
import com.blackbox.cvrt.utils.DateUtils;
import com.blackbox.cvrt.utils.Logger;
import com.blackbox.cvrt.utils.RestUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jerrick Pua
 */
public class UploaderRunnable implements Runnable {

    private static final Logger logger = new Logger( UploaderRunnable.class );
    public static final String RECORD_UPLOAD_API_ENDPOINT = "/api/record/upload";

    private MP3Record record;

    private Location location;

    public UploaderRunnable( MP3Record record, Location location ) {
        this.record = record;
        this.location = location;
    }

    @Override
    public void run() {
        final String host = ApplicationContext.getServerUrl();
        if( StringUtils.isEmpty( host ) ) {
            logger.w(
                    "Server url not configured. Configure to enable android device send data to server" );
            return;
        }

        RestTemplate rest = RestUtils.getRestTemplate();
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add( "file", new FileSystemResource( record.getOutputFile() ) );
        parts.add( "startDate",
                   record.getStartDate().toString( DateUtils.DEFAULT_TIME_STAMP_FORMAT ) );
        parts.add( "endDate",
                   record.getEndDate().toString( DateUtils.DEFAULT_TIME_STAMP_FORMAT ) );
        if( location != null ) {
            parts.add( "longitude", String.valueOf( location.getLongitude() ) );
            parts.add( "latitude", String.valueOf( location.getLatitude() ) );
        }
        logger.i( parts.toString() );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.MULTIPART_FORM_DATA );
        try {
            HttpEntity<MultiValueMap<String, Object>> objectEntity = new HttpEntity<>(
                    parts, headers );
            ResponseEntity<APIResponse> response = rest
                    .exchange( host + RECORD_UPLOAD_API_ENDPOINT, HttpMethod.POST, objectEntity,
                               APIResponse.class );
            logger.i( String.format( "Response from server: %s", response.getBody().toString() ) );
        } catch ( Throwable ex ) {
            logger.i( "Encountered an error while attempting send data to server", ex );
        }
    }
}
