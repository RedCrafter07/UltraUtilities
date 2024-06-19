package redcrafter07.processed.gui.widgets

import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.gui.widgets.ProgressBarWidget.ProgressBarData
import redcrafter07.processed.gui.widgets.ProgressBarWidget.ProgressBarDirection

object ProgressBars {
    val POWERED_FURNACE = down(
        "textures/gui/progress_bars/powered_furnace.png",
        8,
        16,
    )


    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun down(path: String, width: Int, height: Int): ProgressBarData {
        return ProgressBarData(ProcessedMod.rl(path), width, height, ProgressBarDirection.Down)
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun up(path: String, width: Int, height: Int): ProgressBarData {
        return ProgressBarData(ProcessedMod.rl(path), width, height, ProgressBarDirection.Up)
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun right(path: String, width: Int, height: Int): ProgressBarData {
        return ProgressBarData(ProcessedMod.rl(path), width, height, ProgressBarDirection.Right)
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun left(path: String, width: Int, height: Int): ProgressBarData {
        return ProgressBarData(ProcessedMod.rl(path), width, height, ProgressBarDirection.Left)
    }
}