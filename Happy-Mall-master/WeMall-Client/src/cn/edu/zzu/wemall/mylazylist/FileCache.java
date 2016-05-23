package cn.edu.zzu.wemall.mylazylist;

import java.io.File;
import android.content.Context;

public class FileCache {
    
    private static File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"WeMall/ImageCache"); //设置缓存目录,注意此处修改后,缓存清理那里也要同步修改
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    //删除指定url图像的缓存文件
    public static void Filedelete(String url){
    	
    	File f = new File(cacheDir,url.hashCode()+"");
    	f.delete();
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}