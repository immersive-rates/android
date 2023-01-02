package iak.currencyquote.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import iak.currencyquote.R
import iak.currencyquote.databinding.ActivityRepositoriesBinding
import iak.currencyquote.ui.presenter.RepositoryListViewModel
class ActivityRepositoryList : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(): ActivityRepositoryList {
            return ActivityRepositoryList()
        }

        const val TAG = "ActivityRepositoryList"
    }

    private lateinit var binding: ActivityRepositoriesBinding
    private lateinit var repositoryListAdapter: RepositoryListAdapter
    private val presenter: RepositoryListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ActivityRepositoriesBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        with(binding) {
            repositoryListAdapter = RepositoryListAdapter().apply {
                setItems(presenter.getInfos())
                onItemChecked = presenter::onItemChecked
            }

            list.apply {
                adapter = repositoryListAdapter
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        requireContext(),
                        MaterialDividerItemDecoration.VERTICAL
                    ).also {
                        it.isLastItemDecorated = false
                    }
                )
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        presenter.bindAdapter(repositoryListAdapter)

        presenter.getCurrentRepository().observe(viewLifecycleOwner) {
            presenter.onCurrencySourceChanged(it)
        }
    }
}

