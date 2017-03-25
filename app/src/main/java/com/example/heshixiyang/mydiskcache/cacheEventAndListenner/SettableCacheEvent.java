package com.example.heshixiyang.mydiskcache.cacheEventAndListenner;

/**
 * Created by heshixiyang on 2017/3/23.
 */

import android.support.annotation.Nullable;

import com.example.heshixiyang.mydiskcache.cacheKey.CacheKey;

import java.io.IOException;

/**
 * 实现了{@link CacheEvent} ，允许的值设置,支持回收实例
 * (由于内存事件很频繁所以这里用了，享元模式，只提供两个实例，不断的回收重用)
 * Implementation of {@link CacheEvent} that allows the values to be set and supports recycling of
 * instances.
 */
public class SettableCacheEvent implements CacheEvent {

    private static final Object RECYCLER_LOCK = new Object();
    private static final int MAX_RECYCLED = 5;

    private static SettableCacheEvent sFirstRecycledEvent;
    private static int sRecycledCount;

    private CacheKey mCacheKey;
    private String mResourceId;
    private long mItemSize;
    private long mCacheLimit;
    private long mCacheSize;
    private IOException mException;
    private CacheEventListener.EvictionReason mEvictionReason;
    private SettableCacheEvent mNextRecycledEvent;

    public static SettableCacheEvent obtain() {
        synchronized (RECYCLER_LOCK) {
            if (sFirstRecycledEvent != null) {
                SettableCacheEvent eventToReuse = sFirstRecycledEvent;
                sFirstRecycledEvent = eventToReuse.mNextRecycledEvent;
                eventToReuse.mNextRecycledEvent = null;
                sRecycledCount--;
                return eventToReuse;
            }
        }

        return new SettableCacheEvent();
    }

    private SettableCacheEvent() {
    }

    @Nullable
    @Override
    public CacheKey getCacheKey() {
        return mCacheKey;
    }

    public SettableCacheEvent setCacheKey(CacheKey cacheKey) {
        mCacheKey = cacheKey;
        return this;
    }

    @Nullable
    @Override
    public String getResourceId() {
        return mResourceId;
    }

    public SettableCacheEvent setResourceId(String resourceId) {
        mResourceId = resourceId;
        return this;
    }

    @Override
    public long getItemSize() {
        return mItemSize;
    }

    public SettableCacheEvent setItemSize(long itemSize) {
        mItemSize = itemSize;
        return this;
    }

    @Override
    public long getCacheSize() {
        return mCacheSize;
    }

    public SettableCacheEvent setCacheSize(long cacheSize) {
        mCacheSize = cacheSize;
        return this;
    }

    @Override
    public long getCacheLimit() {
        return mCacheLimit;
    }

    public SettableCacheEvent setCacheLimit(long cacheLimit) {
        mCacheLimit = cacheLimit;
        return this;
    }

    @Nullable
    @Override
    public IOException getException() {
        return mException;
    }

    public SettableCacheEvent setException(IOException exception) {
        mException = exception;
        return this;
    }

    @Nullable
    @Override
    public CacheEventListener.EvictionReason getEvictionReason() {
        return mEvictionReason;
    }

    public SettableCacheEvent setEvictionReason(CacheEventListener.EvictionReason evictionReason) {
        mEvictionReason = evictionReason;
        return this;
    }

    public void recycle() {
        synchronized (RECYCLER_LOCK) {
            if (sRecycledCount < MAX_RECYCLED) {
                reset();
                sRecycledCount++;

                if (sFirstRecycledEvent != null) {
                    mNextRecycledEvent = sFirstRecycledEvent;
                }
                sFirstRecycledEvent = this;
            }
        }
    }

    private void reset() {
        mCacheKey = null;
        mResourceId = null;
        mItemSize = 0;
        mCacheLimit = 0;
        mCacheSize = 0;
        mException = null;
        mEvictionReason = null;
    }
}
