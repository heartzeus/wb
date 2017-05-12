package com.tuhanbao.io.impl.classUtil;

import java.util.ArrayList;
import java.util.List;

import com.tuhanbao.io.impl.CodeType;
import com.tuhanbao.io.impl.codeUtil.CodeUtilManager;
import com.tuhanbao.io.objutil.StringUtil;


/**
 * 注释信息
 * 
 * 类，方法，成员变量的注释
 * @author tuhanbao
 *
 */
public class NotesInfo implements Cloneable
{
    private List<String> list;
    
    //缩进tab数
    private int gap = 0;
    
    public NotesInfo(int gap)
    {
        this.gap = gap;
    }
    
    public int size()
    {
        if (list == null)
        {
            return 0;
        }
        return list.size();
    }
    
    @Override
    public String toString()
    {
        //默认返回java格式的注释
        return CodeUtilManager.getCodeUtil(CodeType.JAVA).getNoteStr(this);
    }
    
    @Override
    public NotesInfo clone()
    {
        NotesInfo info = new NotesInfo(this.gap);
        if (list != null)
        {
            for (String s : list)
            {
                info.addNote(s);
            }
        }
        return info;
    }
    
    public List<String> getList()
    {
        return list;
    }

    public void addNote(String s)
    {
    	if (StringUtil.isEmpty(s)) return;
        if (list == null)
        {
            list = new ArrayList<String>();
        }
        this.list.add(s);
    }
    
    public void addNote(NotesInfo notes)
    {
        if (list == null)
        {
            list = new ArrayList<String>();
        }
        
        if (notes != null && notes.getList() != null)
        {
            this.list.addAll(notes.getList());
        }
    }
    
    public void addNoteFromFront(String s)
    {
        if (list == null)
        {
            list = new ArrayList<String>();
        }
        this.list.add(0, s);
    }
    
    public int getGap()
    {
        return gap;
    }

    public void setGap(int gap)
    {
        this.gap = gap;
    }
    
}
