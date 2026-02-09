package simvex.domain.vector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import simvex.domain.vector.domain.AssetCommonPayload;
import simvex.domain.vector.enums.DocType;
import simvex.domain.vector.enums.ToolType;
import simvex.domain.vector.ingest.model.MachineCatalogItem;
import simvex.domain.vector.ingest.model.MachineInfo;
import simvex.domain.vector.ingest.model.PartInfo;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MachineCatalogIngestService {

    private final VectorIngestService ingestService;

    public int ingest(List<MachineCatalogItem> items, String source, int version) {
        if (items == null || items.isEmpty()) {
            throw new CustomException(ErrorCode.VECTOR_INVALID_ARGUMENT);
        }
        if (!StringUtils.hasText(source) || version <= 0) {
            throw new CustomException(ErrorCode.VECTOR_INVALID_ARGUMENT);
        }

        int ingested = 0;
        Instant createdAt = Instant.now();

        for (MachineCatalogItem item : items) {
            if (item == null || !StringUtils.hasText(item.modelName())) {
                continue;
            }

            ToolType toolType = resolveToolType(item.modelName());

            MachineInfo machineInfo = item.machineInfo();
            if (machineInfo != null) {
                String content = buildMachineInfoContent(item.modelName(), machineInfo);
                if (StringUtils.hasText(content)) {
                    AssetCommonPayload payload = new AssetCommonPayload(
                            DocType.KNOWLEDGE,
                            toolType,
                            modelEntityId(toolType),
                            version,
                            source,
                            createdAt
                    );
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("model_name", item.modelName());
                    metadata.put("section", "machine_info");
                    ingestService.ingestText(content, payload, metadata);
                    ingested++;
                }
            }

            if (item.parts() == null) {
                continue;
            }

            for (PartInfo part : item.parts()) {
                if (part == null || !StringUtils.hasText(part.partName())) {
                    continue;
                }
                String content = buildPartContent(item.modelName(), part);
                if (!StringUtils.hasText(content)) {
                    continue;
                }

                AssetCommonPayload payload = new AssetCommonPayload(
                        DocType.PART,
                        toolType,
                        partEntityId(toolType, part.partName()),
                        version,
                        source,
                        createdAt
                );

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("model_name", item.modelName());
                metadata.put("part_name", part.partName());
                metadata.put("section", "part");
                ingestService.ingestText(content, payload, metadata);
                ingested++;
            }
        }

        return ingested;
    }

    private String buildMachineInfoContent(String modelName, MachineInfo info) {
        StringBuilder sb = new StringBuilder();
        appendLine(sb, "Model", modelName);
        appendLine(sb, "Definition", info.definition());
        appendLine(sb, "Material Overview", info.materialOverview());
        appendLine(sb, "Role and Usage", info.roleAndUsage());
        appendLine(sb, "Role and Principle", info.roleAndPrinciple());
        appendLine(sb, "Function", info.function());
        appendLine(sb, "System Impact", info.systemImpact());
        return sb.toString().trim();
    }

    private String buildPartContent(String modelName, PartInfo part) {
        StringBuilder sb = new StringBuilder();
        appendLine(sb, "Model", modelName);
        appendLine(sb, "Part", part.partName());
        appendLine(sb, "Description", part.description());
        appendLine(sb, "Material", part.material());
        appendLine(sb, "Function", part.function());
        appendLine(sb, "Impact", part.impact());
        return sb.toString().trim();
    }

    private void appendLine(StringBuilder sb, String label, String value) {
        if (StringUtils.hasText(value)) {
            sb.append(label).append(": ").append(value.trim()).append('\n');
        }
    }

    private ToolType resolveToolType(String modelName) {
        String normalized = modelName.toUpperCase(Locale.ROOT);
        if (normalized.contains("V4")) {
            return ToolType.V4_ENGINE;
        }
        if (normalized.contains("MACHINE VISE") || normalized.contains("바이스")) {
            return ToolType.MACHINE_VICE;
        }
        if (normalized.contains("ROBOT GRIPPER") || normalized.contains("로봇")) {
            return ToolType.ROBOT_GRIPPER;
        }
        if (normalized.contains("SUSPENSION") || normalized.contains("서스펜션")) {
            return ToolType.SUSPENSION;
        }
        throw new CustomException(ErrorCode.VECTOR_INVALID_ARGUMENT);
    }

    private String modelEntityId(ToolType toolType) {
        return "MODEL:" + toolType.name();
    }

    private String partEntityId(ToolType toolType, String partName) {
        return toolType.name() + "::PART::" + slug(partName);
    }

    private String slug(String input) {
        String upper = input == null ? "" : input.trim().toUpperCase(Locale.ROOT);
        String slug = upper.replaceAll("[^A-Z0-9]+", "_");
        slug = slug.replaceAll("^_+", "").replaceAll("_+$", "");
        return StringUtils.hasText(slug) ? slug : "UNKNOWN";
    }
}
