package com.rs.rslib.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.chad.library.adapter.base.BaseViewHolder;
import com.rs.rslib.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.rs.rslib.widget.BaseCategoryAdapter.CateGoryType.CATEGORY_TYPE_DEFAULT;
import static com.rs.rslib.widget.BaseCategoryAdapter.CateGoryType.CATEGORY_TYPE_LIST;

/**
 * author: xiecong
 * create time: 2018/4/12 15:51
 * lastUpdate time: 2018/4/12 15:51
 * 可以分组显示的Recycleview Adapter 数据类型继承Category即可 用groupId区分所属组
 */

public abstract class BaseCategoryAdapter<G ,V> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_GROUP = 1000;
    public static final int TYPE_ITEM = 1001;
    private CateGoryType mCategoryType = CateGoryType.CATEGORY_TYPE_DEFAULT;
    private boolean canDrag = false;
    private Map<G,Boolean> canDrags = new HashMap<>();

    private Context mContext;
    private int groupResId ;
    private int itemResId;
    private List<V> mData;
    private Map<G,List<V>> mGVMap;

    private List<CategoryEntity<G,V>> mEntityList;
    private RecyclerView mRecyclerView;
    private List<G> mGroups;

    public enum CateGoryType {
         CATEGORY_TYPE_LIST(0),//普通List形式
         CATEGORY_TYPE_DEFAULT(1);//分组形式
         int value;
        CateGoryType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public BaseCategoryAdapter(Context context, int groupResId, int itemResId, List<V> data) {
        mContext = context;
        this.groupResId = groupResId;
        this.itemResId = itemResId;
        mData = data;

    }
    public BaseCategoryAdapter(Context context, int groupResId, int itemResId, Map<G,List<V>> map) {
        mContext = context;
        this.groupResId = groupResId;
        this.itemResId = itemResId;
        bindData(map);
    }

    public BaseCategoryAdapter(Context context, int groupResId, int itemResId) {
        mContext = context;
        this.groupResId = groupResId;
        this.itemResId = itemResId;
    }

    public void setCategoryType(CateGoryType type){
        mCategoryType = type;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }

    public boolean isCanDrag() {
        return canDrag;
    }

    public Map<G, Boolean> getCanDrags() {
        return canDrags;
    }

    public void setCanDrags(Map<G, Boolean> canDrags) {
        this.canDrag = true;
        this.canDrags = canDrags;
    }

    public void bindData(List<V> data){
        mData = data;
        if (mRecyclerView != null) {
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    notifyDataSetChanged();
                    mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }
    /**
     * 绑定组子映射数据
     */
    public void bindData(Map<G,List<V>> map){
        if (map == null) {
            throw new NullPointerException();
        }
        mGVMap = map;
        if (mEntityList == null) {
            mEntityList = new ArrayList<>();
        }
        mEntityList.clear();
        mGroups = new ArrayList<>();
        Set<Map.Entry<G, List<V>>> entrySet = map.entrySet();
        for (Map.Entry<G, List<V>> entry : entrySet) {
            G key = entry.getKey();
            List<V> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                if (!mGroups.contains(key)) {
                    CategoryEntity<G,V> entity = new CategoryEntity<>(key);
                    mEntityList.add(entity);
                    mGroups.add(key);
                }
                for (V value : values) {
                    mEntityList.add(new CategoryEntity<>(key,value));
                }
            }
        }
        if (mRecyclerView != null) {
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    notifyDataSetChanged();
                    mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_GROUP) {
            return new GroupViewHolder(View.inflate(mContext,groupResId,null));
        }else{
            return new ContentViewHolder(View.inflate(mContext,itemResId,null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mCategoryType.getValue() ==  CateGoryType.CATEGORY_TYPE_LIST.getValue()){
            convertItem((BaseViewHolder)holder, mData.get(position));
            return;
        }

        int viewType = getItemViewType(position);
        CategoryEntity<G,V> entity = mEntityList.get(position);
        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;
        if (viewType == TYPE_GROUP) {
            convertGroup(baseViewHolder, entity.getGroup());
        }else if(viewType == TYPE_ITEM){
            convertItem(baseViewHolder, entity.getItem());
            if (baseViewHolder instanceof BaseCategoryAdapter.ContentViewHolder) {
                ContentViewHolder contentViewHolder = (BaseCategoryAdapter.ContentViewHolder) baseViewHolder;
                if (canDrags != null && !canDrags.isEmpty()) {
                    if (canDrags.containsKey(entity.getGroup())) {
                        Boolean aBoolean = canDrags.get(entity.getGroup());
                        contentViewHolder.setCanDrag(aBoolean);
                    }else {
                        contentViewHolder.setCanDrag(false);
                    }
                }
            }
        }
    }

    public CateGoryType getCategoryType(){
        if(mCategoryType == CATEGORY_TYPE_LIST || mEntityList == null){
            return CATEGORY_TYPE_LIST;
        }else{
            return CATEGORY_TYPE_DEFAULT;
        }
    }

    protected abstract void convertGroup(BaseViewHolder holder, G group) ;

    protected abstract void convertItem(BaseViewHolder holder, V t);

    @Override
    public int getItemViewType(int position) {
        if(mCategoryType.getValue() == CATEGORY_TYPE_LIST.getValue() || mEntityList == null){
            return TYPE_ITEM;
        }
        CategoryEntity entity = mEntityList.get(position);
        if (entity.getItem() == null) {
            return TYPE_GROUP;
        }else{
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(mCategoryType.getValue() == CateGoryType.CATEGORY_TYPE_LIST.getValue() || mEntityList == null){
            return mData == null? 0 : mData.size();
        }
        return mEntityList.size();
    }

    public class ContentViewHolder extends BaseViewHolder{
        private boolean canDrag = false;
        public ContentViewHolder(View view) {
            super(view);
        }

        public boolean isCanDrag() {
            return canDrag;
        }

        public void setCanDrag(boolean canDrag) {
            this.canDrag = canDrag;
        }
    }

    public class GroupViewHolder extends BaseViewHolder{

        public GroupViewHolder(View view) {
            super(view);
        }
    }


    public class CategoryEntity<G ,V> {

        private G group;
        private V item;

        public CategoryEntity(G group) {
            this.group = group;
        }

        public CategoryEntity(G group, V item) {
            this.group = group;
            this.item = item;
        }

        public G getGroup() {
            return group;
        }

        public void setGroup(G group) {
            this.group = group;
        }

        public V getItem() {
            return item;
        }

        public void setItem(V item) {
            this.item = item;
        }
    }

    private int[] getDragRange(int position){
        int[] range = new int[2];
        CategoryEntity<G, V> categoryEntity = mEntityList.get(position);
        V item = categoryEntity.item;
        List<V> before = new ArrayList<>();
        for (int i = 0; i < mGroups.size(); i++) {
            G group = mGroups.get(i);
            List<V> vList = mGVMap.get(group);
            int index = vList.indexOf(item);
            if (index != -1) {
                range[0] = before.size() + i + 1;
                range[1] = range[0] + vList.size() - 1;
                break;
            }
            before.addAll(vList);
        }
        return range;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    return viewType == TYPE_GROUP ?  gridLayoutManager.getSpanCount() : 1;
                }
            });
        }

        if (!isCanDrag()) {
            return;
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback(){
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlag = 0;
                int swipFlag = 0;
                if((layoutManager instanceof LinearLayoutManager && !(layoutManager instanceof GridLayoutManager)) || getCategoryType().getValue() == CATEGORY_TYPE_LIST.getValue()){
                    if (layoutManager instanceof LinearLayoutManager && ((LinearLayoutManager)layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                        dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    }else{
                        dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    }
                }else if(getItemViewType(viewHolder.getAdapterPosition()) == TYPE_ITEM){
                    if(viewHolder instanceof BaseCategoryAdapter.ContentViewHolder){
                        ContentViewHolder contentViewHolder = (BaseCategoryAdapter.ContentViewHolder)viewHolder;
                        if (!contentViewHolder.isCanDrag()) {
                            return 0;
                        }
                    }
                    dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                }
                return makeMovementFlags(dragFlag,swipFlag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int currentPosition = viewHolder.getAdapterPosition();
                int targetAdapterPosition = target.getAdapterPosition();
                if((layoutManager instanceof LinearLayoutManager && !(layoutManager instanceof GridLayoutManager)) || getCategoryType().getValue() == CATEGORY_TYPE_LIST.getValue()){
                    if (mData != null && !mData.isEmpty()) {
                        Collections.swap(mData,currentPosition,targetAdapterPosition);
                        notifyItemMoved(currentPosition,targetAdapterPosition);
                    }
                }else{
                    if (mEntityList != null && !mEntityList.isEmpty()) {
                        int[] dragRange = getDragRange(currentPosition);
                        int start = dragRange[0];
                        int end = dragRange[1];
                        LogUtils.info("cong.xie", "onMove: position =" +currentPosition +" start="+start +" end="+end);
                        if (start != 0 && targetAdapterPosition >= start && targetAdapterPosition <= end) {
                            Collections.swap(mEntityList,currentPosition,targetAdapterPosition);
                            notifyItemMoved(currentPosition,targetAdapterPosition);
                        }
                    }
                }
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                return true;
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

}
