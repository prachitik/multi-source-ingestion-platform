package com.pk.fintechbatchloader.api.ingest;

import com.pk.contracts.Source;
import java.util.List;

public record BatchIngestRequest (

       Source source,
       List<BatchIngestItem> events
){
}
