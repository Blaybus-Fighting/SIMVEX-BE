package simvex.domain.vector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import simvex.domain.vector.ingest.model.MachineCatalogItem;
import simvex.domain.vector.api.TextIngestRequest;
import simvex.domain.vector.api.UpdatePayloadRequest;
import simvex.domain.vector.service.PointService;
import simvex.domain.vector.service.MachineCatalogIngestService;
import simvex.domain.vector.service.VectorIngestService;
import simvex.domain.vector.utils.PayloadConverter;
import simvex.global.dto.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vectors")
@RequiredArgsConstructor
public class VectorController {

    private final VectorIngestService ingestService;
    private final PointService pointService;
    private final MachineCatalogIngestService catalogIngestService;

    @PostMapping(value = "/text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Void> ingestText(@Valid @RequestBody TextIngestRequest request) {
        ingestService.ingestText(request.content(), request.payload(), Map.of("ingest_type", "text"));
        return ApiResponse.onSuccess();
    }

    @PostMapping(value = "/points/payload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Map<String, String>> overwritePayload(@Valid @RequestBody UpdatePayloadRequest request) {
        var result = pointService.updatePayload(
                request.entityId(),
                PayloadConverter.toJsonWithIntMap(request.payload())
        );
        return ApiResponse.onSuccess(Map.of("status", result.getStatus().name()));
    }

    @PostMapping("/payload/indexing")
    public ApiResponse<Void> createPayloadIndex(@RequestParam String index) {
        pointService.createPayloadIndex(index);
        return ApiResponse.onSuccess();
    }

    @PostMapping(value = "/catalog", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Map<String, Integer>> ingestCatalog(
            @RequestBody List<MachineCatalogItem> items,
            @RequestParam(defaultValue = "seed_json") String source,
            @RequestParam(defaultValue = "1") int version
    ) {
        int ingested = catalogIngestService.ingest(items, source, version);
        return ApiResponse.onSuccess(Map.of("ingested", ingested));
    }
}
