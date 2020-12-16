package com.like.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.member.dao.MemberDao;
import com.like.mall.member.entity.MemberEntity;
import com.like.mall.member.exception.UserInfoExistException;
import com.like.mall.member.service.MemberService;
import com.like.mall.member.vo.UserLoginVo;
import com.like.mall.member.vo.UserRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    public static void main(String[] args) {
        MemberServiceImpl s = new MemberServiceImpl();
        s.checkEmail("123");
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean checkEmail(String email) {
        getOne(email);
        return true;
    }

    public void getOne(String email) {
        check(email, "email");
    }

    @Override
    public boolean checkMobile(String mobile) {
        check(mobile, "mobile");
        return true;
    }

    @Override
    public boolean checkUsername(String username) {
        check(username, "username");
        return true;
    }

    @Override
    public void register(UserRegisterVo vo) {
        System.out.println("用户注册");
        // 检查用户是否唯一
        checkMobile(vo.getMobile());
        checkUsername(vo.getUsername());

        // 保存
        MemberEntity memberEntity = MemberEntity.builder()
                .username(vo.getUsername())
                .password(new BCryptPasswordEncoder().encode(vo.getPassword()))  // 密码加密
                .mobile(vo.getMobile())
                .levelId(1L)
                .build();
        save(memberEntity);
    }

    @Override
    public MemberEntity login(UserLoginVo vo) {
        String loginName = vo.getLoginName();
        String rawPasswd = vo.getPassword();
        MemberEntity dbMember = getOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginName)
                .or()
                .eq("mobile", loginName)
                .or()
                .eq("email", loginName)
        );
        if (dbMember != null) {
            // 密码匹配
            String encryptedPassword = dbMember.getPassword();
            if (new BCryptPasswordEncoder().matches(rawPasswd, encryptedPassword)) {
                return dbMember;
            }
        }
        return null;
    }

    public void check(String check, String field) {
        MemberEntity one = getOne(new QueryWrapper<MemberEntity>().eq(field, check));
        if (one != null) {
            throw new UserInfoExistException(one);
        }
    }

}