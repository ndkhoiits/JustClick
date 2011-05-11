// simple groovy script
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Dashboard;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.json.JSONObject;

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam

@Path("/my-service")
public class HelloWorld {
  @GET
  @Path("getService")
  public Response getService(@QueryParam("username") String username)
  {
    ArrayList<MessageBean> listBean = new ArrayList<MessageBean>();
    MessageBean kien_nguyen = new MessageBean();
    kien_nguyen.setId("kien_nguyen");
    kien_nguyen.setDisplayName("Nguyen Anh Kien");
    kien_nguyen.setChildren(new ArrayList<MessageBean>());
    kien_nguyen.setData(new Data("Nguyen Anh Kien", "hehe"));
    listBean.add(kien_nguyen);
    
    MessageBean trong_tran = new MessageBean();
    trong_tran.setId("trong_tran");
    trong_tran.setDisplayName("Tran The Trong");
    trong_tran.setData(new Data("Tran The Trong", "hehe"));
    ArrayList<MessageBean> children2 = new ArrayList<MessageBean>();
    children2.add(kien_nguyen);
    trong_tran.setChildren(children2);
    
    listBean.add(trong_tran);
    
    MessageBean phuong_vu = new MessageBean();
    phuong_vu.setId("phuong_vu");
    phuong_vu.setDisplayName("Vu Viet Phuong");
    phuong_vu.setData(new Data("Vu Viet Phuong", "hehe"));
    phuong_vu.setChildren(new ArrayList<MessageBean>());
    
    listBean.add(phuong_vu);
    
    if(username == "kien_nguyen") 
    {
      MessageBean hoang_to = new MessageBean();
      hoang_to.setId("hoang_to");
      hoang_to.setDisplayName("To Minh Hoang");
      hoang_to.setData(new Data("To Minh Hoang", "hehe"));
      ArrayList<MessageBean> children3 = new ArrayList<MessageBean>();
      children3.add(kien_nguyen);
      hoang_to.setChildren(children3);
      
      listBean.add(hoang_to);
      listBean.remove(kien_nguyen);
    }
    
    if (username == 'hoang_to') {
      listBean.remove(hoang_to);
    }
    else if (username == 'trong_tran') {
      listBean.remove(trong_tran);
    }
    else if (username == 'phuong_vu') {
      listBean.remove(phuong_vu);
    }
    
    return renderJSON(listBean, username);
  }
  
  private Response renderJSON(List<HashMap<String, String>> listBean, String id)
  {
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    MessageBean data = new MessageBean();
    data.setId(id);
    data.setDisplayName("Current User");
    data.setChildren(listBean);
    
    return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
  }
}
  
  class MessageBean {
    private List<Object> children;
    
    private String id;
    
    private String displayName;
    
    private Data data;
    
    public List<Object> getChildren()
    {
      return children;
    }
    
    public void setChildren(List<Object> children)
    {
      this.children = children;
    }
    
    public String getId()
    {
      return id;
    }
    
    public void setId(String id)
    {
      this.id = id;
    }
    
    public String getDisplayName()
    {
      return displayName;
    }
    
    public void setDisplayName(String displayName)
    {
      this.displayName = displayName;
    }
    
    public void setData(Data data)
    {
      this.data = data;
    }
    
    public Data getData()
    {
      return this.data;
    }
  }
  
class Data {
  private String displayName;
  private String avatar;
  
  public Data(String displayName, avatar)
  {
    this.displayName = displayName;
    this.avatar = avatar;
  }
  
  public String getDisplayName() {
    return displayName;  
  }
  
  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }
  
  public String getAvatar() {
    return this.avatar;  
  }
  
  public void setAvatar(String avatar)
  {
    this.avatar = avatar;
  }
}
