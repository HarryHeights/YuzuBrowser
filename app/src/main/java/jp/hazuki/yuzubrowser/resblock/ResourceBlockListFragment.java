package jp.hazuki.yuzubrowser.resblock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import jp.hazuki.yuzubrowser.BrowserApplication;
import jp.hazuki.yuzubrowser.R;
import jp.hazuki.yuzubrowser.resblock.checker.NormalChecker;
import jp.hazuki.yuzubrowser.utils.view.DeleteDialog;
import jp.hazuki.yuzubrowser.utils.view.recycler.ArrayRecyclerAdapter;
import jp.hazuki.yuzubrowser.utils.view.recycler.OnRecyclerListener;
import jp.hazuki.yuzubrowser.utils.view.recycler.RecyclerFabFragment;
import jp.hazuki.yuzubrowser.utils.view.recycler.SimpleViewHolder;

/**
 * Created by hazuki on 17/02/28.
 */

public class ResourceBlockListFragment extends RecyclerFabFragment implements OnRecyclerListener, CheckerEditDialog.OnCheckerEdit, DeleteDialog.OnDelete {
    private ResourceBlockManager mManager;
    private ResBlockAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        mManager = new ResourceBlockManager(getActivity().getApplicationContext());
        adapter = new ResBlockAdapter(getActivity(), mManager.getList(), this);
        setRecyclerViewAdapter(adapter);

        return getRootView();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, int fromIndex, int toIndex) {
        adapter.move(fromIndex, toIndex);
        return true;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        mManager.save(BrowserApplication.getInstance());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, final int index) {
        final ResourceChecker checker = mManager.remove(index);
        adapter.notifyDataSetChanged();
        Snackbar.make(getRootView(), R.string.deleted, Snackbar.LENGTH_SHORT)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mManager.add(index, checker);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            mManager.save(BrowserApplication.getInstance());
                        }
                    }
                })
                .show();
    }

    @Override
    public void onRecyclerItemClicked(View v, int position) {
        showEditDialog(position, (NormalChecker) mManager.get(position));
    }

    @Override
    public boolean onRecyclerItemLongClicked(View v, int position) {
        DeleteDialog.newInstance(getActivity(), R.string.confirm, R.string.resblock_confirm_delete, position)
                .show(getChildFragmentManager(), "delete");
        return true;
    }

    @Override
    public void onDelete(int position) {
        mManager.remove(position);
        mManager.save(getActivity().getApplicationContext());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onAddButtonClick() {
        showEditDialog(-1, null);
    }

    private void showEditDialog(int index, NormalChecker checker) {
        CheckerEditDialog.newInstance(index, checker)
                .show(getChildFragmentManager(), "edit");
    }

    @Override
    public void onCheckerEdited(int index, ResourceChecker checker) {
        if (index >= 0) {
            mManager.set(index, checker);
        } else {
            mManager.add(checker);
        }
        mManager.save(BrowserApplication.getInstance());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(R.string.sort).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean next = !adapter.isSortMode();
                adapter.setSortMode(next);

                Toast.makeText(getActivity(), (next) ? R.string.start_sort : R.string.end_sort, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return adapter.isSortMode();
    }

    private static class ResBlockAdapter extends ArrayRecyclerAdapter<ResourceChecker, SimpleViewHolder> {
        private Context context;

        ResBlockAdapter(Context context, List<ResourceChecker> list, OnRecyclerListener listener) {
            super(context, list, listener);
            this.context = context;
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, ResourceChecker item, int position) {
            holder.textView.setText(item.getTitle(context));
        }

        @Override
        protected SimpleViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return new SimpleViewHolder(inflater.inflate(R.layout.simple_recycler_list_item_1, parent, false),
                    android.R.id.text1);
        }
    }
}
