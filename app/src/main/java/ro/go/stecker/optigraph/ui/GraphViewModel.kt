package ro.go.stecker.optigraph.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ro.go.stecker.optigraph.algs.completeGraphGenerator
import ro.go.stecker.optigraph.algs.randomGraphGenerator
import ro.go.stecker.optigraph.algs.randomTreeGenerator
import ro.go.stecker.optigraph.data.GenerationType
import ro.go.stecker.optigraph.data.UiState

class GraphViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun selectGenerationType(type: GenerationType) {
        _uiState.update { it.copy(selectedGeneration = type) }
    }

    fun setNumberNodes(number: Int) {
        _uiState.update { it.copy(nodes = number) }
    }

    fun generateGraph() {
        when(_uiState.value.selectedGeneration) {
            GenerationType.RandomTree -> {
                _uiState.update { it.copy(edges = randomTreeGenerator(_uiState.value.nodes)) }
            }
            GenerationType.CompleteGraph -> {
                _uiState.update { it.copy(edges = completeGraphGenerator(_uiState.value.nodes)) }
            }
            GenerationType.RandomGraph -> {
                _uiState.update { it.copy(edges = randomGraphGenerator(_uiState.value.nodes)) }
            }
        }
    }
}