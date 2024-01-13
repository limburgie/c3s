package be.webfactor.c3s.controller.helper.asset;

import be.webfactor.c3s.controller.PageController;
import be.webfactor.c3s.controller.sass.SassCompiler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;

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

    public Asset getAsset(HttpServletRequest request, String basePath) throws IOException {
        String requestUri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String assetPath = StringUtils.removeStart(requestUri, PageController.ASSETS_PREFIX);
        String assetUrl = basePath + PageController.ASSETS_PREFIX + assetPath;
        byte[] data = getAssetData(basePath, assetUrl, assetPath);

        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, assetPath);
        MediaType contentType = MediaType.valueOf(TIKA_CONFIG.getDetector().detect(TikaInputStream.get(data), metadata).toString());

        return new Asset(data, contentType);
    }

    private byte[] getAssetData(String basePath, String assetUrl, String assetPath) throws IOException {
        try {
            return IOUtils.toByteArray(new URL(assetUrl));
        } catch(IOException e) {
            if (assetPath.endsWith(".css")) {
                String relativeDirectory = assetPath.substring(0, assetPath.lastIndexOf("/") + 1);
                String sassAssetPath = assetPath.replace(".css", ".scss");

                SassCompiler sassCompiler = new SassCompiler(basePath, relativeDirectory);

                return sassCompiler.compile(IOUtils.toByteArray(new URL(basePath + PageController.ASSETS_PREFIX + sassAssetPath)));
            } else {
                throw e;
            }
        }
    }
}
