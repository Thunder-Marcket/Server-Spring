
package com.example.demo.src.users;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.users.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexPhoneNum;

@AllArgsConstructor
@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

//    /**
//     * 회원 조회 API
//     * [GET] /users
//     * 회원 번호 및 이메일 검색 조회 API
//     * [GET] /users? Email=
//     * @return BaseResponse<List<GetUserRes>>
//     */
//    //Query String
//    @ResponseBody
//    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
//    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
//        try{
//            if(Email == null){
//                List<GetUserRes> getUsersRes = userProvider.getUsers();
//                return new BaseResponse<>(getUsersRes);
//            }
//            // Get Users
//            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
//            return new BaseResponse<>(getUsersRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx ?salesStatus=
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetMyPageRes> getUser(@PathVariable("userIdx") int userIdx,
                                              @RequestParam int salesStatus) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserRes getUserRes = userProvider.getUser(userIdx);
            List<GetUserItemRes> getUserItemResList = userProvider.getUserItems(userIdx, salesStatus);
            GetMyPageRes getMyPageRes = new GetMyPageRes(getUserRes, getUserItemResList);
            return new BaseResponse<>(getMyPageRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users/sign-up
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        if (postUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUM);
        }
        if (!isRegexPhoneNum(postUserReq.getPhoneNumber())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) throws BaseException {
        try{
            PostLoginRes postLoginRes = userService.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<PatchUserRes> modifyUser(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserReq patchUserReq){
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchUserRes patchUserRes = userService.modifyUser(userIdx, patchUserReq);
            return new BaseResponse<>(patchUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 탈퇴하기 API
     * [PATCH] /users/d/:userIdx
     *
     * @return BaseResponse<PatchUserStatusRes>
     */
    @ResponseBody
    @PatchMapping("/d/{userIdx}")
    public BaseResponse<PatchUserRes> modifyUserStatus(@PathVariable("userIdx") int userIdx) throws BaseException {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchUserRes patchUserRes = userService.modifyUserStatus(userIdx);
            return new BaseResponse<>(patchUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}