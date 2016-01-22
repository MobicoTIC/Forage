package io.github.plastix.forage.ui.cachelist;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.plastix.forage.ForageApplication;
import io.github.plastix.forage.R;
import io.github.plastix.forage.ui.SimpleDividerItemDecoration;

/**
 * Fragment that is responsible for the Geocache list.
 */
public class CacheListFragment extends Fragment implements CacheListView, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    CacheListPresenter presenter;

    @Inject
    CacheAdapter adapter;

    @Inject
    SimpleDividerItemDecoration itemDecorator;

    @Bind(R.id.cachelist_recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.cachelist_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.empty_view)
    View emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Tell the activity we have menu items to contribute to the toolbar
        setHasOptionsMenu(true);

        injectDependencies();
    }

    private void injectDependencies() {
        ForageApplication.getComponent(getContext())
                .plus(new CacheListModule(this)).injectTo(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cache_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateEmptyView();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void updateEmptyView() {
        stopRefresh();
        if (adapter.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

    }

    private void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onErrorInternet() {
        stopRefresh();
        Snackbar.make(recyclerView, R.string.cachelist_error_no_internet, Snackbar.LENGTH_LONG)
                .setAction(R.string.cachelist_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadGeocaches();
                    }
                }).show();
    }

    private void downloadGeocaches() {
        swipeRefreshLayout.setRefreshing(true);
        presenter.getCaches();
    }

    @Override
    public void onErrorLocation() {
        stopRefresh();
        Snackbar.make(recyclerView, R.string.cachelist_error_no_location, Snackbar.LENGTH_LONG)
                .setAction(R.string.cachelist_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadGeocaches();
                    }
                }).show();
    }

    @Override
    public void onRefresh() {
        downloadGeocaches();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items from the Fragment's menu
        inflater.inflate(R.menu.menu_cache_list_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fetch:
                downloadGeocaches();
                return true;
            case R.id.action_clear:
                presenter.clearCaches();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Call the onStop() lifecycle callback of the presenter.
     */
    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    /**
     * Call the onStart() lifecycle callback of the presenter.
     */
    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.closeRealm();
    }
}
