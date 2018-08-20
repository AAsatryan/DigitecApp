package digitec.arsen.digitecproject.adater

import android.content.Context
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digitec.arsen.digitecproject.R
import digitec.arsen.digitecproject.model.DataModel
import kotlinx.android.synthetic.main.file_item.view.*


class FileAdapter(val context: Context, val data: ArrayList<DataModel>) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        if (data[position].fileType == "jpg"||data[position].fileType =="png"){
            holder.fileImage.setImageBitmap(BitmapFactory.decodeFile(data[position].fileUri))
        }else{
            val thumb = ThumbnailUtils.createVideoThumbnail(data[position].fileUri, MediaStore.Video.Thumbnails.MINI_KIND)
            holder.fileImage.setImageBitmap(thumb)
        }
        holder.removeFile.setOnClickListener {
            removeItem(position)
        }
        holder.fileName.text = data[position].fileName
        holder.fileSize.text = data[position].fileSize.toString()
    }


    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileImage = view.image!!
        val fileName = view.myFileName!!
        val fileSize = view.myFileSize!!
        val removeFile = view.remove!!
    }

    fun updateData(list: ArrayList<DataModel>) {
        data.addAll(list)
        notifyDataSetChanged()

    }

    private fun removeItem(position: Int){
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,data.size)
    }
}