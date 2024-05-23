package ru.myitschool.lab23.core

import android.view.Choreographer

interface MetricsService {

    fun startTraceRecord()
    fun stopTraceRecord(trackingInfo: String)

    class PerformanceMetricsService(
        private val performanceListener: PerformanceListener
    ) : MetricsService {

        private var frameTime = 0L
        private val frameCallback = Choreographer.FrameCallback { frameTimeNanos ->
            onFrameRendered(frameTimeNanos)
            frameTime = frameTimeNanos
        }

        override fun startTraceRecord() {
            performanceListener.onNewRound()
            with(Choreographer.getInstance()) {
                removeFrameCallback(frameCallback)
                postFrameCallback(frameCallback)
            }
        }

        override fun stopTraceRecord(trackingInfo: String) {
            Choreographer.getInstance().removeFrameCallback(frameCallback)
            performanceListener.sendPerformanceMetrics(trackingInfo)
        }

        private fun onFrameRendered(frameTimeNanos: Long) {
            performanceListener.recordPerformance(frameTimeNanos - frameTime)
        }
    }

    class PerformanceListener(
        private val sendMetrics: (trackingInfo: String, framesTimeRendered: List<Long>) -> Unit
    ) {
        private var gatheredData = ArrayList<Long>()

        fun recordPerformance(renderedTimeNanos: Long) {
            gatheredData.add(renderedTimeNanos)
        }

        fun sendPerformanceMetrics(trackingInfo: String) {
            sendMetrics(trackingInfo, gatheredData)
            onNewRound()
        }

        fun onNewRound() {
            gatheredData = ArrayList()
        }
    }
}
