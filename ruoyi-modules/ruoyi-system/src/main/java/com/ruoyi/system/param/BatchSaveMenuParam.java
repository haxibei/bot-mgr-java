package com.ruoyi.system.param;

import com.ruoyi.system.domain.SysMenu;
import lombok.Data;

import java.util.List;


@Data
public class BatchSaveMenuParam {

    private List<SysMenu> menus;
}
