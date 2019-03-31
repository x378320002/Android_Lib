package com.wzx.wzxfoundation.headerfooterrecycleradapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

abstract class HeaderFooterRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
	private static final int VIEW_TYPE_MAX_COUNT = 10000000;
	private static final int HEADER_VIEW_TYPE_OFFSET = 0;
	private static final int CONTENT_VIEW_TYPE_OFFSET = HEADER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT;
	private static final int FOOTER_VIEW_TYPE_OFFSET = CONTENT_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT;

	private int mHeaderItemCount;
	private int mContentItemCount;
	private int mFooterItemCount;

	/**
	 * {@inheritDoc}
	 * <br/>
	 * <br/>
	 * 通过这个viewType, 即要区分开是header还是footer, 还要能确认出是footer和header的第几个
	 * 所以这个viewType是一个大范围内的数字, header[0, 10000000],
	 * content[10000000, 2*10000000],
	 * footer[2*10000000, 3*10000000],
	 */
	@Override
	public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
		// Delegate to proper methods based on the viewType ranges.
		if (viewType >= HEADER_VIEW_TYPE_OFFSET && viewType < HEADER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
			return onCreateHeaderItemViewHolder(parent, viewType - HEADER_VIEW_TYPE_OFFSET);
		} else if (viewType >= FOOTER_VIEW_TYPE_OFFSET && viewType < FOOTER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
			return onCreateFooterItemViewHolder(parent, viewType - FOOTER_VIEW_TYPE_OFFSET);
		} else if (viewType >= CONTENT_VIEW_TYPE_OFFSET && viewType < CONTENT_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
			return onCreateContentItemViewHolder(parent, viewType - CONTENT_VIEW_TYPE_OFFSET);
		} else {
			// This shouldn't happen as we check that the viewType provided by
			// the client is valid.
			throw new IllegalStateException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onBindViewHolder(VH viewHolder, int position) {
		// Delegate to proper methods based on the viewType ranges.
		if (mHeaderItemCount > 0 && position < mHeaderItemCount) {
			onBindHeaderItemViewHolder(viewHolder, position);
		} else if (mContentItemCount > 0 && position - mHeaderItemCount < mContentItemCount) {
			onBindContentItemViewHolder(viewHolder, position - mHeaderItemCount);
		} else {
			onBindFooterItemViewHolder(viewHolder, position - mHeaderItemCount - mContentItemCount);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getItemCount() {
		// Cache the counts and return the sum of them.
		mHeaderItemCount = getHeaderItemCount();
		mContentItemCount = getContentItemCount();
		mFooterItemCount = getFooterItemCount();
		return mHeaderItemCount + mContentItemCount + mFooterItemCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getItemViewType(int position) {
		// Delegate to proper methods based on the position, but validate first.
		if (mHeaderItemCount > 0 && position < mHeaderItemCount) {
			return validateViewType(getHeaderItemViewType(position)) + HEADER_VIEW_TYPE_OFFSET;
		} else if (mContentItemCount > 0 && position - mHeaderItemCount < mContentItemCount) {
			return validateViewType(getContentItemViewType(position - mHeaderItemCount)) + CONTENT_VIEW_TYPE_OFFSET;
		} else {
			return validateViewType(getFooterItemViewType(position - mHeaderItemCount - mContentItemCount)) + FOOTER_VIEW_TYPE_OFFSET;
		}
	}

	/**
	 * Validates that the view type is within the valid range.
	 *
	 * @param viewType
	 *            the view type.
	 * @return the given view type.
	 */
	private int validateViewType(int viewType) {
		if (viewType < 0 || viewType >= VIEW_TYPE_MAX_COUNT) {
			throw new IllegalStateException("viewType must be between 0 and " + VIEW_TYPE_MAX_COUNT);
		}
		return viewType;
	}

	/**
	 * Notifies that a header item is inserted.
	 *
	 * @param position
	 *            the position of the header item.
	 */
	public final void notifyHeaderItemInserted(int position) {
		int newHeaderItemCount = getHeaderItemCount();
		if (position < 0 || position >= newHeaderItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for header items [0 - "
					+ (newHeaderItemCount - 1) + "].");
		}
		mHeaderItemCount = getHeaderItemCount();
		try {
			notifyItemInserted(position);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Notifies that multiple header items are inserted.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyHeaderItemRangeInserted(int positionStart, int itemCount) {
		int newHeaderItemCount = getHeaderItemCount();
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > newHeaderItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for header items [0 - " + (newHeaderItemCount - 1) + "].");
		}
		mHeaderItemCount = getHeaderItemCount();
		try {
			notifyItemRangeInserted(positionStart, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a header item is changed.
	 *
	 * @param position
	 *            the position.
	 */
	public final void notifyHeaderItemChanged(int position) {
		mHeaderItemCount = getHeaderItemCount();
		if (position < 0 || position >= mHeaderItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for header items [0 - "
					+ (mHeaderItemCount - 1) + "].");
		}
		try {
			notifyItemChanged(position);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that multiple header items are changed.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyHeaderItemRangeChanged(int positionStart, int itemCount) {
		mHeaderItemCount = getHeaderItemCount();
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount >= mHeaderItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for header items [0 - " + (mHeaderItemCount - 1) + "].");
		}
		try {
			notifyItemRangeChanged(positionStart, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that an existing header item is moved to another position.
	 *
	 * @param fromPosition
	 *            the original position.
	 * @param toPosition
	 *            the new position.
	 */
	public void notifyHeaderItemMoved(int fromPosition, int toPosition) {
		if (fromPosition < 0 || toPosition < 0 || fromPosition >= mHeaderItemCount || toPosition >= mHeaderItemCount) {
			throw new IndexOutOfBoundsException("The given fromPosition " + fromPosition + " or toPosition " + toPosition
					+ " is not within the position bounds for header items [0 - " + (mHeaderItemCount - 1) + "].");
		}
		mHeaderItemCount = getHeaderItemCount();
		try {
			notifyItemMoved(fromPosition, toPosition);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a header item is removed.
	 *
	 * @param position
	 *            the position.
	 */
	public void notifyHeaderItemRemoved(int position) {
		if (position < 0 || position >= mHeaderItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for header items [0 - "
					+ (mHeaderItemCount - 1) + "].");
		}
		mHeaderItemCount = getHeaderItemCount();
		try {
			notifyItemRemoved(position);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that multiple header items are removed.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public void notifyHeaderItemRangeRemoved(int positionStart, int itemCount) {
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > mHeaderItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for header items [0 - " + (mHeaderItemCount - 1) + "].");
		}
		mHeaderItemCount = getHeaderItemCount();
		try {
			notifyItemRangeRemoved(positionStart, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a content item is inserted.
	 *
	 * @param position
	 *            the position of the content item.
	 */
	public final void notifyContentItemInserted(int position) {
		int newHeaderItemCount = getHeaderItemCount();
		int newContentItemCount = getContentItemCount();
		if (position < 0 || position >= newContentItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for content items [0 - "
					+ (newContentItemCount - 1) + "].");
		}
		try {
			notifyItemInserted(position + newHeaderItemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that multiple content items are inserted.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyContentItemRangeInserted(int positionStart, int itemCount) {
		int newHeaderItemCount = getHeaderItemCount();
		int newContentItemCount = getContentItemCount();
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > newContentItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for content items [0 - " + (newContentItemCount - 1) + "].");
		}
		try {
			notifyItemRangeInserted(positionStart + newHeaderItemCount, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a content item is changed.
	 *
	 * @param position
	 *            the position.
	 */
	public final void notifyContentItemChanged(int position) {
		if (position < 0 || position >= mContentItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for content items [0 - "
					+ (mContentItemCount - 1) + "].");
		}
		try {
			notifyItemChanged(position + mHeaderItemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that multiple content items are changed.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyContentItemRangeChanged(int positionStart, int itemCount) {
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > mContentItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for content items [0 - " + (mContentItemCount - 1) + "].");
		}
		try {
			notifyItemRangeChanged(positionStart + mHeaderItemCount, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that an existing content item is moved to another position.
	 *
	 * @param fromPosition
	 *            the original position.
	 * @param toPosition
	 *            the new position.
	 */
	public final void notifyContentItemMoved(int fromPosition, int toPosition) {
		if (fromPosition < 0 || toPosition < 0 || fromPosition >= mContentItemCount || toPosition >= mContentItemCount) {
			throw new IndexOutOfBoundsException("The given fromPosition " + fromPosition + " or toPosition " + toPosition
					+ " is not within the position bounds for content items [0 - " + (mContentItemCount - 1) + "].");
		}
		try {
			notifyItemMoved(fromPosition + mHeaderItemCount, toPosition + mHeaderItemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a content item is removed.
	 *
	 * @param position
	 *            the position.
	 */
	public final void notifyContentItemRemoved(int position) {
		if (position < 0 || position >= mContentItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for content items [0 - "
					+ (mContentItemCount - 1) + "].");
		}
		try {
			notifyItemRemoved(position + mHeaderItemCount);
		} catch (Throwable e) {
		}

	}

	/**
	 * Notifies that multiple content items are removed.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyContentItemRangeRemoved(int positionStart, int itemCount) {
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > mContentItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for content items [0 - " + (mContentItemCount - 1) + "].");
		}
		try {
			notifyItemRangeRemoved(positionStart + mHeaderItemCount, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a footer item is inserted.
	 *
	 * @param position
	 *            the position of the content item.
	 */
	public final void notifyFooterItemInserted(int position) {
		int newHeaderItemCount = getHeaderItemCount();
		int newContentItemCount = getContentItemCount();
		int newFooterItemCount = getFooterItemCount();
		if (position < 0 || position >= newFooterItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for footer items [0 - "
					+ (newFooterItemCount - 1) + "].");
		}
		try {
			notifyItemInserted(position + newHeaderItemCount + newContentItemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that multiple footer items are inserted.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyFooterItemRangeInserted(int positionStart, int itemCount) {
		int newHeaderItemCount = getHeaderItemCount();
		int newContentItemCount = getContentItemCount();
		int newFooterItemCount = getFooterItemCount();
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > newFooterItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for footer items [0 - " + (newFooterItemCount - 1) + "].");
		}
		try {
			notifyItemRangeInserted(positionStart + newHeaderItemCount + newContentItemCount, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a footer item is changed.
	 *
	 * @param position
	 *            the position.
	 */
	public final void notifyFooterItemChanged(int position) {
		mFooterItemCount = getFooterItemCount();
		if (position < 0 || position >= mFooterItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for footer items [0 - "
					+ (mFooterItemCount - 1) + "].");
		}
		try {
			notifyItemChanged(position + mHeaderItemCount + mContentItemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that multiple footer items are changed.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyFooterItemRangeChanged(int positionStart, int itemCount) {
		mFooterItemCount = getFooterItemCount();
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > mFooterItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for footer items [0 - " + (mFooterItemCount - 1) + "].");
		}
		try {
			notifyItemRangeChanged(positionStart + mHeaderItemCount + mContentItemCount, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that an existing footer item is moved to another position.
	 *
	 * @param fromPosition
	 *            the original position.
	 * @param toPosition
	 *            the new position.
	 */
	public final void notifyFooterItemMoved(int fromPosition, int toPosition) {
		if (fromPosition < 0 || toPosition < 0 || fromPosition >= mFooterItemCount || toPosition >= mFooterItemCount) {
			throw new IndexOutOfBoundsException("The given fromPosition " + fromPosition + " or toPosition " + toPosition
					+ " is not within the position bounds for footer items [0 - " + (mFooterItemCount - 1) + "].");
		}
		mFooterItemCount = getFooterItemCount();
		try {
			notifyItemMoved(fromPosition + mHeaderItemCount + mContentItemCount, toPosition + mHeaderItemCount + mContentItemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that a footer item is removed.
	 *
	 * @param position
	 *            the position.
	 */
	public final void notifyFooterItemRemoved(int position) {
		if (position < 0 || position >= mFooterItemCount) {
			throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for footer items [0 - "
					+ (mFooterItemCount - 1) + "].");
		}
		mFooterItemCount = getFooterItemCount();
		try {
			notifyItemRemoved(position + mHeaderItemCount + mContentItemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Notifies that multiple footer items are removed.
	 *
	 * @param positionStart
	 *            the position.
	 * @param itemCount
	 *            the item count.
	 */
	public final void notifyFooterItemRangeRemoved(int positionStart, int itemCount) {
		if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > mFooterItemCount) {
			throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1)
					+ "] is not within the position bounds for footer items [0 - " + (mFooterItemCount - 1) + "].");
		}
		mFooterItemCount = getFooterItemCount();
		try {
			notifyItemRangeRemoved(positionStart + mHeaderItemCount + mContentItemCount, itemCount);
		} catch (Throwable e) {
		}
	}

	/**
	 * Gets the header item view type. By default, this method returns 0.
	 *
	 * @param position
	 *            the position.
	 * @return the header item view type (within the range [0 -
	 *         VIEW_TYPE_MAX_COUNT-1]).
	 */
	protected int getHeaderItemViewType(int position) {
		return 0;
	}

	/**
	 * Gets the header item count. This method can be called several times, so
	 * it should not calculate the count every time.
	 *
	 * @return the header item count.
	 */
	protected abstract int getHeaderItemCount();

	/**
	 * This method works exactly the same as
	 * {@link #onCreateViewHolder(ViewGroup, int)}, but for header
	 * items.
	 *
	 * @param parent
	 *            the parent view.
	 * @param headerViewType
	 *            the view type for the header.
	 * @return the view holder.
	 */
	protected abstract VH onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType);

	/**
	 * This method works exactly the same as
	 * {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
	 * , but for header items.
	 *
	 * @param headerViewHolder
	 *            the view holder for the header item.
	 * @param position
	 *            the position.
	 */
	protected abstract void onBindHeaderItemViewHolder(VH headerViewHolder, int position);

	/**
	 * Gets the footer item view type. By default, this method returns 0.
	 *
	 * @param position
	 *            the position.
	 * @return the footer item view type (within the range [0 -
	 *         VIEW_TYPE_MAX_COUNT-1]).
	 */
	protected int getFooterItemViewType(int position) {
		return 0;
	}

	/**
	 * Gets the footer item count. This method can be called several times, so
	 * it should not calculate the count every time.
	 *
	 * @return the footer item count.
	 */
	protected abstract int getFooterItemCount();

	/**
	 * This method works exactly the same as
	 * {@link #onCreateViewHolder(ViewGroup, int)}, but for footer
	 * items.
	 *
	 * @param parent
	 *            the parent view.
	 * @param footerViewType
	 *            the view type for the footer.
	 * @return the view holder.
	 */
	protected abstract VH onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType);

	/**
	 * This method works exactly the same as
	 * {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
	 * , but for footer items.
	 *
	 * @param footerViewHolder
	 *            the view holder for the footer item.
	 * @param position
	 *            the position.
	 */
	protected abstract void onBindFooterItemViewHolder(VH footerViewHolder, int position);

	/**
	 * Gets the content item view type. By default, this method returns 0.
	 *
	 * @param position
	 *            the position.
	 * @return the content item view type (within the range [0 -
	 *         VIEW_TYPE_MAX_COUNT-1]).
	 */
	protected int getContentItemViewType(int position) {
		return 0;
	}

	/**
	 * Gets the content item count. This method can be called several times, so
	 * it should not calculate the count every time.
	 *
	 * @return the content item count.
	 */
	protected abstract int getContentItemCount();

	/**
	 * This method works exactly the same as
	 * {@link #onCreateViewHolder(ViewGroup, int)}, but for content
	 * items.
	 *
	 * @param parent
	 *            the parent view.
	 * @param contentViewType
	 *            the view type for the content.
	 * @return the view holder.
	 */
	protected abstract VH onCreateContentItemViewHolder(ViewGroup parent, int contentViewType);

	/**
	 * This method works exactly the same as
	 * {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
	 * , but for content items.
	 *
	 * @param contentViewHolder
	 *            the view holder for the content item.
	 * @param position
	 *            the position.
	 */
	protected abstract void onBindContentItemViewHolder(VH contentViewHolder, int position);


	/**
	 * 解决GridLayoutManager问题, 头和脚横向应该占用一行
	 */
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		if (layoutManager != null) {
			if (layoutManager instanceof GridLayoutManager) {
				final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
				gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
					@Override
					public int getSpanSize(int position) {
						if (getMyItemViewType(position) == 1) {
							return 1;
						}
						return gridLayoutManager.getSpanCount();
					}
				});
			}
		}
	}

	/**
	 * 解决瀑布流布局问题, 头和脚横向应该占用一行
	 */
	@Override
	public void onViewAttachedToWindow(VH holder) {
		super.onViewAttachedToWindow(holder);
		int position = holder.getLayoutPosition();
		if (getMyItemViewType(position) == 1) {
			ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
			if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
				StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;
				layoutParams.setFullSpan(false);
			}
		} else {
			ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
			if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
				StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;
				layoutParams.setFullSpan(true);
			}
		}
	}

	/**
	 * 自己用来区分是头,尾,还是内容的, getItemViewType是供系统用的<br/>
	 * 0代表头, 1代表内容, 2代表尾
	 */
	public int getMyItemViewType(int position) {
		// Delegate to proper methods based on the position, but validate first.
		if (mHeaderItemCount > 0 && position < mHeaderItemCount) {
			return 0;
		} else if (mContentItemCount > 0 && position - mHeaderItemCount < mContentItemCount) {
			return 1;
		} else {
			return 2;
		}
	}
}