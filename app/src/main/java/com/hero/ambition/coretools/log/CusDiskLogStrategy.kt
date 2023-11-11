package com.hero.ambition.coretools.log

import android.os.Handler
import com.orhanobut.logger.DiskLogStrategy

class CusDiskLogStrategy(handler: Handler): DiskLogStrategy(handler) {
}