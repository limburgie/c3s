package be.webfactor.c3s.controller.helper.asset;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.controller.sass.SassCompiler;
import be.webfactor.c3s.master.service.MasterAssetNotFoundException;
import be.webfactor.c3s.master.service.MasterService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;

@Service
public class AssetService {

    private static final TikaConfig TIKA_CONFIG;

    static {
        try {
            TIKA_CONFIG = new TikaConfig();
        } catch (TikaException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Asset getAsset(HttpServletRequest request, MasterService masterService) throws IOException {
        String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String assetPath = StringUtils.removeStart(requestUri, PageController.ASSETS_PREFIX);
        byte[] data = getAssetData(masterService, assetPath);

        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, assetPath);
        MediaType contentType = MediaType.valueOf(TIKA_CONFIG.getDetector().detect(TikaInputStream.get(data), metadata).toString());

        return new Asset(data, contentType);
    }

    private byte[] getAssetData(MasterService masterService, String assetPath) {
        try {
            return masterService.readAsset(PageController.ASSETS_PREFIX + assetPath);
        } catch (MasterAssetNotFoundException e) {
            if (assetPath.endsWith(".css")) {
                String relativeDirectory = assetPath.substring(0, assetPath.lastIndexOf("/") + 1);
                String sassAssetPath = assetPath.replace(".css", ".scss");

                SassCompiler sassCompiler = new SassCompiler(masterService, relativeDirectory);

                return sassCompiler.compile(masterService.readAsset(PageController.ASSETS_PREFIX + sassAssetPath));
            } else {
                throw e;
            }
        }
    }
}
