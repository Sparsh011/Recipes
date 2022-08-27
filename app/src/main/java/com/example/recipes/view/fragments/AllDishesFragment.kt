package com.example.recipes.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recipes.R
import com.example.recipes.databinding.FragmentAllDishesBinding
import com.example.recipes.view.activities.AddUpdateDish
import com.example.recipes.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_add_dish ->{
                startActivity(Intent(requireActivity(), AddUpdateDish::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

//    private lateinit var _binding: FragmentAllDishesBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
        val root = inflater.inflate(R.layout.fragment_all_dishes, container, false)

        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }
}