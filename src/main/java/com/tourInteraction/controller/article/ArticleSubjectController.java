package com.tourInteraction.controller.article;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tourInteraction.controller.SignInAndUpController;
import com.tourInteraction.entity.User;
import com.tourInteraction.entity.article.ArticleSubject;
import com.tourInteraction.service.article.IArticleSubjectService;
import com.tourInteraction.utils.JSONUtil;
import com.tourInteraction.utils.UUIDUitl;

@Controller
@RequestMapping("/articleSubject")
public class ArticleSubjectController {
	
	@Resource(name="articleSubjectServiceImpl")
	private IArticleSubjectService	articleSubjectService;
	
	@RequestMapping("/getSubject.do")
	public @ResponseBody String getSubject(String limit,String offset){
		System.out.println("articleSubject/getSubject.do被调用");

		List<ArticleSubject> list = new ArrayList<ArticleSubject>();
		list = articleSubjectService.getSubject(limit,offset);
		Map map = new HashMap<String , Object>();
		map.put("list", list);
		map.put("count", 10);
		String result = JSONUtil.map2json(map);
		return result;
	}
	
	@RequestMapping("getSubjectById.do")
	public @ResponseBody String getSubjectById( @RequestParam("id") int id){
		ArticleSubject articleSubject = new ArticleSubject();
		System.out.println("articleSubject/getSubjectById.do被调用");
		articleSubject = articleSubjectService.getSubjectById(id);
	
		String result = JSONUtil.object2json(articleSubject);
		return result;
	}
	
	@RequestMapping("delSubjectById.do")
	public @ResponseBody String delSubjectById( @RequestParam("id") int id){
		System.out.println("articleSubject/delSubjectById.do被调用");
		
		int num = articleSubjectService.delSubjectById(id);
		String result = "删除失败!";
		if(num > 0){
			result = "删除成功!";
		}
		return result;
	}
	
	@RequestMapping("/updateSubject.do")
	public @ResponseBody String updateSubject(HttpServletRequest req, 
			@RequestParam("id") int id,
			@RequestParam("SubjectName") String SubjectName, 
			@RequestParam("SubjectDiscription") String SubjectDiscription,
			@RequestParam("SubjectLabel") String SubjectLabel,
			@RequestParam("SubjectIcon") String SubjectIcon){
		System.out.println("articleSubject/updateSubject.do被调用");
		User user = SignInAndUpController.getSignInUser(req);
		Map<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("id", id);
		mapParam.put("SubjectName", SubjectName);
		mapParam.put("SubjectDiscription", SubjectDiscription);
		mapParam.put("SubjectLabel", SubjectLabel);
		mapParam.put("SubjectIcon", SubjectIcon);
		mapParam.put("updateTime", new Date());
		mapParam.put("updateUser", user.getId());
		mapParam.put("status", "1");

		int num = articleSubjectService.updateSubject(mapParam);
		String result = "修改失败";
		if(num>0){
			result = "修改成功";
			return result;
		}
		return result;
	}
	
	@RequestMapping("/addSubject.do")
	public @ResponseBody String addSubject(HttpServletRequest req, 
			@RequestParam("SubjectName") String SubjectName, 
			@RequestParam("SubjectDiscription") String SubjectDiscription,
			@RequestParam("SubjectLabel") String SubjectLabel,
			@RequestParam("SubjectIcon") String SubjectIcon){
		System.out.println("articleSubject/addSubject.do被调用");
		User user = SignInAndUpController.getSignInUser(req);
		Map<String, Object> mapParam = new HashMap<String, Object>();
		mapParam.put("SubjectName", SubjectName);
		mapParam.put("SubjectDiscription", SubjectDiscription);
		mapParam.put("SubjectLabel", SubjectLabel);
		mapParam.put("SubjectIcon", SubjectIcon);
	
		mapParam.put("createTime", new Date());
		mapParam.put("createUser", user.getId());
		mapParam.put("status", "1");
		
		int num = articleSubjectService.addSubject(mapParam);
		String result = "增加失败";
		if(num>0){
			result = "增加成功";
			return result;
		}
		return result;
	}
	
	 /**
     * 上传
     * @param 
     * @return
     */
    @RequestMapping(value = "/uploadFile.do")
    public @ResponseBody String uploadFile(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("X-Frame-Options","SAMEORIGIN");
        System.out.println("invoke----------/articleSubject/uploadFile.do");
        List<String> result = new ArrayList<String>();
        List<Long> rs=new ArrayList<Long>();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd/HH");
        /**构建文件保存的目录**/
        String logoPathDir = "/files";
        /**得到文件保存目录的真实路径**/
        String logoRealPathDir = request.getServletContext().getRealPath(logoPathDir);
            try{
                /**根据真实路径创建目录**/
                File logoSaveFile = new File(logoRealPathDir);
                if (!logoSaveFile.exists())
                    logoSaveFile.mkdirs();
            }catch (Exception e){
                e.printStackTrace();
            }
    
        /**页面控件的文件流**/
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multiRequest.getFile("file");
        if (multiRequest.getMultiFileMap().size() == 0) {
            return "未选择文件或未指定文件参数名";
        }
        String retPath = "";
        String retrunPath="";
        if (multipartFile != null) {
            /**获取文件的后缀**/
            String suffix = multipartFile.getOriginalFilename().substring
                    (multipartFile.getOriginalFilename().lastIndexOf("."));
            /**使用UUID生成文件名称**/
            String logImageName = UUIDUitl.generateLowerString(32) + suffix;//构建文件名称
            /**拼成完整的文件保存路径加文件**/
            String fileName = logoPathDir + File.separator + logImageName;
            //返回给客户端的路径
            retPath = logoRealPathDir + File.separator + logImageName;
            File file = new File(retPath);
            try {
                multipartFile.transferTo(file);
                fileName= fileName.replaceAll("\\\\", "/");
                retrunPath=fileName;
                result.add(fileName);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // }
        System.out.println(retrunPath);
        return retrunPath;
    }

}