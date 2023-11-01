package es.unex.giis.asee.gepeto.view.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.unex.giis.asee.gepeto.R
import es.unex.giis.asee.gepeto.adapters.ItemSwapAdapter
import es.unex.giis.asee.gepeto.data.Session
import es.unex.giis.asee.gepeto.data.todosLosIngredientes
import es.unex.giis.asee.gepeto.databinding.FragmentIngredientesBinding
import es.unex.giis.asee.gepeto.utils.filtrarLista
import java.util.TreeSet

/**
 * A simple [Fragment] subclass.
 * Use the [IngredientesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IngredientesFragment : Fragment() {

    private lateinit var _binding: FragmentIngredientesBinding

    private lateinit var todosIngredientesAdapter: ItemSwapAdapter
    private lateinit var ingredientesSeleccionadosAdapter: ItemSwapAdapter

    private val binding get() = _binding

    private lateinit var filtroIngredientes : EditText

    private fun getIngredientes () : TreeSet<String> {
        val ingredientes = Session.getValue("ingredientesSeleccionados") as TreeSet<*>? ?: TreeSet<String>()
        val ingredientesFiltrados = TreeSet<String>(todosLosIngredientes)

        if (ingredientes.isEmpty()) {
            return ingredientesFiltrados
        }

        for ( item in ingredientes ) {
            ingredientesFiltrados.remove(item)
        }

        return ingredientesFiltrados
    }

    // Esta lista almacenará todos los cambios que se hagan en la lista de todos los ingredientes
    private var listaIngredientes : TreeSet<String> = getIngredientes()
    // Utilizo un treeset porque no admite duplicados y los elementos están ordenados automaticamente

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIngredientesBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAllRecyclerView()
        setUpSelectedRecyclerView()

        filtrarLista(
            binding.buscadorDeIngredientes,
            listaIngredientes,
            todosIngredientesAdapter
        )
    }

    private fun setUpAllRecyclerView () {
        todosIngredientesAdapter = ItemSwapAdapter(
            itemSet = TreeSet<String>(todosLosIngredientes),
            onClick = {

            ingredientesSeleccionadosAdapter.add(it)
            todosIngredientesAdapter.remove(it)
            listaIngredientes.remove(it)

            Session.setValue("ingredientesSeleccionados", ingredientesSeleccionadosAdapter.getSet())
        })

        with(binding.rvTodosIngredientes) {
            adapter = todosIngredientesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setUpSelectedRecyclerView () {
        ingredientesSeleccionadosAdapter = ItemSwapAdapter(
            itemSet = Session.getValue("ingredientesSeleccionados") as TreeSet<String>? ?: TreeSet<String>(),
            onClick = {

            todosIngredientesAdapter.add(it)
            ingredientesSeleccionadosAdapter.remove(it)
            listaIngredientes.add(it)

            Session.setValue("ingredientesSeleccionados", ingredientesSeleccionadosAdapter.getSet())
        })

        with(binding.rvIngredientesSeleccionados) {
            adapter = ingredientesSeleccionadosAdapter
            layoutManager = GridLayoutManager(context,3)
        }
    }
}