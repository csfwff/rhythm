package org.b3log.symphony.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.processor.middleware.validate.Activity1A0001CollectValidationMidware;
import org.b3log.symphony.processor.middleware.validate.Activity1A0001ValidationMidware;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.util.Symphonys;

import java.util.Map;

/**
 * 《龙门阵处理器》
 *  由于龙门阵的特殊需求，仅在内存中保存聊天记录，故不连接 Service.
 */
@Singleton
public class IdleTalkProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(IdleTalkProcessor.class);

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;



    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);

        final IdleTalkProcessor idleTalkProcessor = beanManager.getReference(IdleTalkProcessor.class);
        Dispatcher.get("/idle-talk", idleTalkProcessor::showIdleTalk, loginCheck::handle);
    }

    /**
     * Shows Idle Talk index.
     *
     * @param context
     */
    public void showIdleTalk(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "home/idle-talk.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }
}
