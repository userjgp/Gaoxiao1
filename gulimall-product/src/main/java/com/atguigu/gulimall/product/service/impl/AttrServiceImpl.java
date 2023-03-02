package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;

import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.Attrvo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Autowired
    public AttrAttrgroupRelationDao relationDao;
@Autowired
CategoryService categoryService;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    AttrGroupDao attrGroupDao;
@Autowired
AttrDao attrDao;
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrEntity> list = new ArrayList<AttrEntity>();
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",attrgroupId));
        if (attrAttrgroupRelationEntities==null || attrAttrgroupRelationEntities.size()==0) return null;
        if(attrAttrgroupRelationEntities!=null && attrAttrgroupRelationEntities.size()>0){
            for (AttrAttrgroupRelationEntity attrAttrgroupRelationEntity : attrAttrgroupRelationEntities) {
                Long attrId = attrAttrgroupRelationEntity.getAttrId();
                AttrEntity attrEntity = attrDao.selectById(attrId);
                list.add(attrEntity);
            }

        }

        return list;


    }
    /**
     //     * 获取当前分组没有被关联的所有属性
     //     * @param params
     //     * @param attrgroupId
     //     * @return
     //     */


    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }



    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> parmas, Long cateLogId,String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(type) ?ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (cateLogId != 0) {
            queryWrapper.eq("catelog_id", cateLogId);
        }
        String key = (String) parmas.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(parmas), queryWrapper);
        PageUtils pages = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
//            设置分组和分类的名字
            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null&&attrId.getAttrGroupId()!=null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity category = categoryDao.selectById(attrEntity.getCatelogId());
            if (category != null) {
                attrRespVo.setCatelogName(category.getName());
            }


            return attrRespVo;

        })).collect(Collectors.toList());

        pages.setList(respVos);
        return pages;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {

            AttrRespVo attrRespVo = new AttrRespVo();
            AttrEntity byId = this.getById(attrId);
            BeanUtils.copyProperties(byId,attrRespVo);
    //设置分组信息
        if (byId.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {

            AttrAttrgroupRelationEntity attrid1 = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrid1 != null) {
                Long attrGroupId = attrid1.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                if (attrGroupEntity != null)
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
    //        设置分类信息
            Long catelogId = byId.getCatelogId();
            Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        if(categoryEntity!=null){
            attrRespVo.setCatelogName(categoryEntity.getName());
        }


            return attrRespVo;
    }
    @Transactional
    @Override
    public void updateAttr(Attrvo attrvo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrvo,attrEntity);
        this.updateById(attrEntity);
//        修改分组关联
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() ){ AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrvo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrvo.getAttrId());
            Integer attr_id = relationDao.selectCount(new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrvo.getAttrId()));
            if(attr_id>0){
                relationDao.update(attrAttrgroupRelationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrvo.getAttrId()));

            }else {

                relationDao.insert(attrAttrgroupRelationEntity);
            }}



    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {

        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());

        relationDao.deleteBatchRelation(entities);

    }


//
//    @Resource
//    private AttrGroupDao attrGroupDao;
//
//    @Resource
//    private CategoryDao categoryDao;
//
//    @Resource
//    private CategoryService categoryService;
//
//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<AttrEntity> page = this.page(
//                new Query<AttrEntity>().getPage(params),
//                new QueryWrapper<AttrEntity>()
//        );
//
//        return new PageUtils(page);
//    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(Attrvo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //1、保存基本数据
        this.save(attrEntity);

        //2、保存关联关系
        //判断类型，如果是基本属性就设置分组id
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
            AttrAttrgroupRelationEntity attre = new AttrAttrgroupRelationEntity();
            attre.setAttrGroupId(attr.getAttrGroupId());
            attre.setAttrId(attrEntity.getAttrId());
            relationDao.insert(attre);}
        }



//    @Override
//    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
//
//        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
//                .eq("attr_type","base".equalsIgnoreCase(attrType) ?
//                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
//
//        //根据catelogId查询信息
//        if (catelogId != 0) {
//            queryWrapper.eq("catelog_id",catelogId);
//        }
//
//        String key = (String) params.get("key");
//        if (!StringUtils.isEmpty(key)) {
//            //attr_id attr_name
//            queryWrapper.and((wrapper) -> {
//               wrapper.eq("attr_id",key).or().like("attr_name",key);
//            });
//        }
//
//        IPage<AttrEntity> page = this.page(
//                new Query<AttrEntity>().getPage(params),
//                queryWrapper
//        );
//
//        PageUtils pageUtils = new PageUtils(page);
//        List<AttrEntity> records = page.getRecords();
//
//        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
//            AttrRespVo attrRespVo = new AttrRespVo();
//            BeanUtils.copyProperties(attrEntity, attrRespVo);
//
//            //设置分类和分组的名字
//            if ("base".equalsIgnoreCase(attrType)) {
//                AttrAttrgroupRelationEntity relationEntity =
//                        relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
//                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
//                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
//                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
//                }
//
//            }
//
//            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
//            if (categoryEntity != null) {
//                attrRespVo.setCatelogName(categoryEntity.getName());
//
//            }
//            return attrRespVo;
//        }).collect(Collectors.toList());
//
//        pageUtils.setList(respVos);
//        return pageUtils;
//
//    }
//
//    @Override
//    public AttrRespVo getAttrInfo(Long attrId) {
//
//        //查询详细信息
//        AttrEntity attrEntity = this.getById(attrId);
//
//        //查询分组信息
//        AttrRespVo respVo = new AttrRespVo();
//        BeanUtils.copyProperties(attrEntity,respVo);
//
//        //判断是否是基本类型
//        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
//            //1、设置分组信息
//            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne
//                    (new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
//            if (relationEntity != null) {
//                respVo.setAttrGroupId(relationEntity.getAttrGroupId());
//                //获取分组名称
//                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
//                if (attrGroupEntity != null) {
//                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
//                }
//            }
//        }
//
//        //2、设置分类信息
//        Long catelogId = attrEntity.getCatelogId();
//        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
//
//        respVo.setCatelogPath(catelogPath);
//        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
//        if (categoryEntity != null) {
//            respVo.setCatelogName(categoryEntity.getName());
//        }
//
//        return respVo;
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void updateAttrById(AttrVo attr) {
//
//        AttrEntity attrEntity = new AttrEntity();
//        BeanUtils.copyProperties(attr,attrEntity);
//
//        this.updateById(attrEntity);
//
//        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
//            //1、修改分组关联
//            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
//            relationEntity.setAttrGroupId(attr.getAttrGroupId());
//            relationEntity.setAttrId(attr.getAttrId());
//
//            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
//                    .eq("attr_id", attr.getAttrId()));
//
//            if (count > 0) {
//                relationDao.update(relationEntity,
//                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
//            } else {
//                relationDao.insert(relationEntity);
//            }
//        }
//
//    }
//
//    /**
//     * 根据分组id找到关联的所有属性
//     * @param attrgroupId
//     * @return
//     */
//    @Override
//    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
//
//        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList
//                (new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
//
//        List<Long> attrIds = entities.stream().map((attr) -> {
//            return attr.getAttrId();
//        }).collect(Collectors.toList());
//
//        //根据attrIds查找所有的属性信息
//        //Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
//
//        //如果attrIds为空就直接返回一个null值出去
//        if (attrIds == null || attrIds.size() == 0) {
//            return null;
//        }
//
//        List<AttrEntity> attrEntityList = this.baseMapper.selectBatchIds(attrIds);
//
//        return attrEntityList;
//    }
//
//    @Override
//    public void deleteRelation(AttrGroupRelationVo[] vos) {
//        //relationDao.delete(new QueryWrapper<>().eq("attr_id",1L).eq("attr_group_id",1L));
//
//        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
//            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
//            BeanUtils.copyProperties(item, relationEntity);
//            return relationEntity;
//        }).collect(Collectors.toList());
//
//        relationDao.deleteBatchRelation(entities);
//    }
//
//    /**
//     * 获取当前分组没有被关联的所有属性
//     * @param params
//     * @param attrgroupId
//     * @return
//     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {

        //1、当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //获取当前分类的id
        Long catelogId = attrGroupEntity.getCatelogId();

        //2、当前分组只能关联别的分组没有引用的属性
        //2.1）、当前分类下的其它分组
        List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));

        //获取到所有的attrGroupId
        List<Long> collect = groupEntities.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());


        //2.2）、这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList
                (new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));

        List<Long> attrIds = groupId.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        //2.3）、从当前分类的所有属性移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

        if (attrIds != null && attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }

        //判断是否有参数进行模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        PageUtils pageUtils = new PageUtils(page);


        return pageUtils;
    }
//
//    @Override
//    public List<Long> selectSearchAttrs(List<Long> attrIds) {
//
//        List<Long> searchAttrIds = this.baseMapper.selectSearchAttrIds(attrIds);
//
//        return searchAttrIds;
//    }

}