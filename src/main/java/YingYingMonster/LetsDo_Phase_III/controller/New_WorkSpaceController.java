package YingYingMonster.LetsDo_Phase_III.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import YingYingMonster.LetsDo_Phase_III.entity.Image;
import YingYingMonster.LetsDo_Phase_III.entity.Project;
import YingYingMonster.LetsDo_Phase_III.entity.Tag;
import YingYingMonster.LetsDo_Phase_III.entity.TextNode;
import YingYingMonster.LetsDo_Phase_III.service.ProjectService;
import YingYingMonster.LetsDo_Phase_III.service.WorkerService;


@Controller
@RequestMapping("/workspace")
public class New_WorkSpaceController {
	
	@Autowired
	private WorkerService wkservice;
	
	@Autowired
	ProjectService pjservice;
	
	ArrayList<Image> picture_list = new ArrayList<Image>();//暂存
	
	private static int number_of_a_page = 5;
	
	/**
	 * 
	 * @return 返回标注的界面
	 */
	@GetMapping("/make/{workType}")
	public String getWorkSpace(@PathVariable("workType")String type) {
		if(type.equals("area")) {
			//填写标签，包括整体标注
			return "workspace/newarea";
		}
		else if(type.equals("square")) {
			//按要求覆盖指定区域
			return "workspace/newsquare";
		}	
		else {
			//default
			return "error";
		}
	}
	
	/*
	 * 返回一些图片的id，下划线分割
	 */
	@ResponseBody
	@GetMapping("/getsomeimages")
	public String getSomeImages(HttpServletRequest request, HttpServletResponse response) {
		String res = "";
	
		String projectId = request.getParameter("projectId");
		String pageNumber = request.getParameter("pageNumber");
		
    	long pjid = Long.parseLong(projectId);
    	int pageNum = Integer.parseInt(pageNumber);
    	
    	picture_list = (ArrayList<Image>) wkservice.getAPageOfImage(pjid,pageNum);//pageNum参数不给出，给0
		int len = picture_list.size();
		for(int i=0;i<len;i++) {
			res += picture_list.get(i).getId();
			if(i != len-1) {
				res += "_";
			}
		}
		
		return res;//如果没有了，返回""
	}
	
	/**
	 * @return返回总共的页数
	 */
	@ResponseBody
	@GetMapping("/getTotalPageNum")
	public String getTotalPageNumber(HttpServletRequest request, HttpServletResponse response) {
		
		String projectId = request.getParameter("projectId");
		
    	long pjid = Long.parseLong(projectId);
    	
    	picture_list = (ArrayList<Image>) wkservice.getAllImages(pjid);
		int len = picture_list.size();//总共图片数量
		
		int page_num = len / number_of_a_page;
		if(len % number_of_a_page != 0) {
			page_num++;
		}
		System.out.println("PAGE NUM: "+page_num);
		return page_num+"";//
	}
	
   /**
   	* 把一张等待完成的图片放到url里
 * @throws IOException 
   	*/
    @RequestMapping("/getNewPicture/{pictureId}")  
    @ResponseBody  
	public void getNewImage(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("pictureId")String pictureId) throws IOException {
    	long picId = Long.parseLong(pictureId);
    	
    	String JPG="image/jpeg;charset=UTF-8";  
    	
    	for(Image image : picture_list) {
    		if(image.getId() == picId) {
    			byte[] data = image.getPicture();
    			OutputStream outputStream = response.getOutputStream();  
    			response.setContentType(JPG);  
                outputStream.write(data);  
                outputStream.flush();   
                outputStream.close();  
                break;
    		}
    	}
	}
    
    /**
     * 返回  width_height
     * @param pictureId
     * @return
     */
    @RequestMapping("/getPictureSize/{pictureId}")  
    @ResponseBody  
    public String getPictureSize(@PathVariable("pictureId")String pictureId) {
    	long picId = Long.parseLong(pictureId);
    	int width = 0;
    	int height = 0;
    	for(Image image : picture_list) {
    		if(image.getId() == picId) {
    			width = image.getWidth();
    			height = image.getHeight();
                break;
    		}
    	}
    	return width+"_"+height;
    }
    
    /**DONE
	 * 上传一个tag
	 * @param request
	 * @param response
	 */
	@PostMapping("/submit")
	public void submitTag(HttpServletRequest request, HttpServletResponse response) {
		
		String imageDataURL = request.getParameter("base64");//tag图片的64位编码
		String xml = request.getParameter("xml");//xml
		System.out.println("XML: "+xml);
		System.out.println("LENGTH: "+xml.length());
		String userId = request.getParameter("userId");
		String pictureId = request.getParameter("pictureId");
		String projectId = request.getParameter("projectId");
		Base64.Decoder mdecoder = Base64.getDecoder();
    	byte[] mb = mdecoder.decode(imageDataURL);
    	
    	long uid = Long.parseLong(userId);
    	long picid = Long.parseLong(pictureId);
    	long pjid = Long.parseLong(projectId);
    	
    	Tag tag = new Tag(uid,picid,pjid,mb,xml,false);
    	
    	wkservice.uploadTag(tag);
    	
	}
	
    /**
     * 返回项目要求
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getRequirement")  
    @ResponseBody 
    public String get_requirement(HttpServletRequest request, HttpServletResponse response) {
    	String pjId = request.getParameter("projectId");
    	long projectId = Long.parseLong(pjId);
		Project project = wkservice.getAProject(projectId);
		
		String req = project.getTagRequirement();
		return req;
    }
	
    
    /**
     * 
     * @param request
     * @param response
     * @return  class1!class2!...!classn
     *          classx:   classname:selection1,selection2,...,selectionn
     *          selectionx:   selectiontitle_opt1_opt2_..._optn
     *          猪:肥瘦_肥_瘦,大小_大_小!牛:性别_雄_雌,种类_耕牛_奶牛    
     */
    @RequestMapping("/getOptions")  
    @ResponseBody 
    public String get_options(HttpServletRequest request, HttpServletResponse response) {
    	String pjId = request.getParameter("projectId");
    	long projectId = Long.parseLong(pjId);
		Project pj = pjservice.getAProject(projectId);
		String res = "";
		List<TextNode> list = pjservice.getProjectTextNode(projectId);//一textnode对应一个class
		
		int classNum = list.size();
		
		for(int i=0;i<classNum;i++) {
			
			String classi = list.get(i).getName()+":";
			List<String> selections = list.get(i).getAttributions();
			
			for(int j=0;j<selections.size();j++) {
				String sel_j = selections.get(j);
				String[] sel_j_split = sel_j.split(":");
				String selectionj = sel_j_split[0]+"_"+sel_j_split[1];
				classi += selectionj;
				if(j != selections.size()-1) {
					classi += ",";
				}
			}
			
			res += classi;
			if(i != classNum-1) {
				res += "!";
			}
		}
		return res;
    }
}

