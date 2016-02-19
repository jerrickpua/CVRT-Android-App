package com.blackbox.cvrt.core.thread;

import android.location.Location;

import com.blackbox.cvrt.core.model.MP3Record;
import com.blackbox.cvrt.core.model.Record;
import com.blackbox.cvrt.utils.Logger;
import com.blackbox.cvrt.utils.RestUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
        final String host = "http://192.168.0.102:8080";
        RestTemplate rest = RestUtils.getRestTemplate();
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add( "file", new FileSystemResource( record.getOutputFile() ) );
        parts.add( "startDate",
                   Record.RECORDER_TIMESTAMP_FORMAT.print( record.getStartDate() ) );
        parts.add( "endDate", Record.RECORDER_TIMESTAMP_FORMAT.print(
                record.getEndDate() ) );
        if( location != null ) {
            parts.add( "longitude", String.valueOf( location.getLongitude() )  );
            parts.add( "latitude", String.valueOf(  location.getLatitude() ) );
        }
        logger.i( parts.toString() );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.MULTIPART_FORM_DATA );
        try {
            HttpEntity<MultiValueMap<String, Object>> objectEntity = new HttpEntity<>(
                    parts, headers );
            ResponseEntity<String> response = rest
                    .exchange( host + RECORD_UPLOAD_API_ENDPOINT, HttpMethod.POST, objectEntity,
                               String.class );
            logger.i( String.format( "Received object from url: ", response ) );
        } catch ( Throwable ex ) {
            logger.i( "Encountered an error while attempting send data to server", ex );
        }
    }
}
