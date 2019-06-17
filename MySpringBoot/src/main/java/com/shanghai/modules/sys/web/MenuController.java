package com.shanghai.modules.sys.web;

import java.util.List;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shanghai.common.persistence.BaseController;
import com.shanghai.common.utils.constant.SysConstants;
import com.shanghai.modules.sys.entity.Menu;
import com.shanghai.modules.sys.service.MenuService;

/**
* @author: YeJR 
* @version: 2018年6月26日 下午4:53:27
* 
*/
@Controller
@RequestMapping(value = "sys/menu")
public class MenuController extends BaseController{
	
	@Autowired
	private MenuService menuService;
	
	/**
	 * list 列表
	 * @param menu
	 * @param model
	 * @return
	 */
	// [注]:这里的permissions应该和x-springboot的一样
	@RequestMapping(value = "list")
	@RequiresPermissions(value="sys:list:menu")
	public String list(Menu menu, Model model) {
		List<Menu> list = menuService.findAllMenus(new Menu());
		model.addAttribute("list", list);
		return "modules/sys/menuList";
	}
	
	/**
	 * form表单
	 * @param menu
	 * @param model
	 * @return
	 */
	// [注]:logical=Logical.OR应该是只要满足一个权限就行
	@RequestMapping(value = "form")
	@RequiresPermissions(value= {"sys:add:menu", "sys:view:menu", "sys:edit:menu"}, logical=Logical.OR)
	public String form(Menu menu, Model model) {
		if (menu.getId() != null) {
			menu = menuService.getMenuById(menu);
		}
		// 添加下级菜单，需要获取父菜单信息
		if (menu.getParentId() != null) {
			Menu parent = new Menu(menu.getParentId());
			menu.setParent(menuService.getMenuById(parent));
		}
		model.addAttribute("menu", menu);
		return "modules/sys/menuForm";
	}
	
	/**
	 * 添加或者编辑
	 * @param menu
	 * @param redirectAttributes
	 * @return
	 */
	// [注]:RedirectAttributes一般配合最后的redirect使用,保存参数再跳转
	@RequestMapping(value = "save")
	@RequiresPermissions(value= {"sys:add:menu", "sys:edit:menu"}, logical=Logical.OR)
	public String save(Menu menu, RedirectAttributes redirectAttributes) {
		// [注]:根据id是否存在,在一个方法里做C和U
		if (menu.getId() == null) {
			menuService.saveMenu(menu);
		} else {
			menuService.updateMenu(menu);
		}
		// [注]:addFlashAttribute只能让页面的el表达式获取值,可能去了另一个Controller的方法里是没法获取的,需要参考https://www.cnblogs.com/zhujiabin/p/4935557.html
		redirectAttributes.addFlashAttribute("msg", SysConstants.OPERATE_SUCCESS_PAGE_TIP);
		return "redirect:/sys/menu/list";
	}

	/**
	 * 单个删除
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions(value="sys:delete:menu")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		menuService.deleteMenuById(id);
		redirectAttributes.addFlashAttribute("msg", SysConstants.OPERATE_SUCCESS_PAGE_TIP);
		return "redirect:/sys/menu/list";
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "deleteAll")
	@RequiresPermissions(value="sys:delete:menu")
	public String deleteAll(Integer[] ids, RedirectAttributes redirectAttributes) {
		menuService.deletemenuByIds(ids);
		redirectAttributes.addFlashAttribute("msg", SysConstants.OPERATE_SUCCESS_PAGE_TIP);
		return "redirect:/sys/menu/list";
	}
	
	/**
	 * 进入选择父菜单页面
	 * @param menu
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "menuSelect")
	public String menuSelect (Menu menu, Model model) {
		model.addAttribute("menuList", menuService.findAllMenus(menu));
		return "modules/sys/menuSelect";
	}
	
}
