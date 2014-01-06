package jp.sf.fess.dict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jp.sf.fess.dict.synonym.SynonymFile;

import org.seasar.extension.timer.TimeoutManager;
import org.seasar.extension.timer.TimeoutTarget;
import org.seasar.extension.timer.TimeoutTask;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictionaryManager {
    private static final Logger logger = LoggerFactory
            .getLogger(DictionaryManager.class);

    protected Map<String, DictionaryFile<? extends DictionaryItem>> dicFileMap;

    public long keepAlive = 5 * 60 * 1000; // 5min

    public int watcherTimeout = 60; // 1min

    protected volatile long lifetime = 0;

    protected TimeoutTask watcherTargetTask;

    protected List<DictionaryLocator> locatorList = new ArrayList<DictionaryLocator>();

    @InitMethod
    public void init() {
        // start
        final WatcherTarget watcherTarget = new WatcherTarget();
        watcherTargetTask = TimeoutManager.getInstance().addTimeoutTarget(
                watcherTarget, watcherTimeout, true);
    }

    @DestroyMethod
    public void destroy() {
        if (watcherTargetTask != null && !watcherTargetTask.isStopped()) {
            watcherTargetTask.stop();
        }
    }

    public DictionaryFile<? extends DictionaryItem>[] getDictionaryFiles() {
        final Map<String, DictionaryFile<? extends DictionaryItem>> fileMap = getDictionaryFileMap();

        final Collection<DictionaryFile<? extends DictionaryItem>> values = fileMap
                .values();
        return values.toArray(new SynonymFile[values.size()]);
    }

    public DictionaryFile<? extends DictionaryItem> getDictionaryFile(
            final String id) {
        if (StringUtil.isBlank(id)) {
            return null;
        }

        final Map<String, DictionaryFile<? extends DictionaryItem>> fileMap = getDictionaryFileMap();
        return fileMap.get(id);
    }

    protected Map<String, DictionaryFile<? extends DictionaryItem>> getDictionaryFileMap() {
        synchronized (this) {
            if (lifetime > System.currentTimeMillis() && dicFileMap != null) {
                lifetime = System.currentTimeMillis() + keepAlive;
                return dicFileMap;
            }

            final Map<String, DictionaryFile<? extends DictionaryItem>> newFileMap = new HashMap<String, DictionaryFile<? extends DictionaryItem>>();
            for (final DictionaryLocator locator : locatorList) {
                for (final DictionaryFile<? extends DictionaryItem> dictFile : locator
                        .find()) {
                    final String id = UUID.randomUUID().toString();
                    dictFile.setId(id);
                    newFileMap.put(id, dictFile);
                }
            }
            dicFileMap = newFileMap;
            lifetime = System.currentTimeMillis() + keepAlive;
            return dicFileMap;
        }
    }

    public void addLocator(final DictionaryLocator locator) {
        locatorList.add(locator);
    }

    protected class WatcherTarget implements TimeoutTarget {
        @Override
        public void expired() {
            synchronized (DictionaryManager.this) {
                if (lifetime <= System.currentTimeMillis()
                        && dicFileMap != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cleaning synonym files: " + dicFileMap);
                    }
                    dicFileMap = null;
                }
            }
        }
    }
}