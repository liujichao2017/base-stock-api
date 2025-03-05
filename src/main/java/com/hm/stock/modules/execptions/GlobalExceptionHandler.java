package com.hm.stock.modules.execptions;

import com.hm.stock.modules.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
@Order
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error(CommonResultCode.INTERNAL_ERROR);
    }

//    @ExceptionHandler(NoResourceFoundException.class)
//    @ResponseBody
//    public Result seriousHandler(Exception e) {
//        log.debug("调试错误: {}",e.getMessage(),e);
//        return Result.error(CommonResultCode.AUT_ERROR);
//    }


    @ExceptionHandler(InternalException.class)
    @ResponseBody
    public Result logicHandler(InternalException e) {
        log.info("业务异常: {}",e.getMessage(),e);
        return Result.error(e.getResultCode(),e.getParams());
    }

    /**
     * 参数效验异常处理器
     *
     * @param e 参数验证异常
     * @return ResponseInfo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result parameterExceptionHandler(MethodArgumentNotValidException e) {
        // 获取异常信息
        BindingResult exceptions = e.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                FieldError fieldError = (FieldError) errors.get(0);
                log.debug("请求接口参数异常: {}", fieldError.getDefaultMessage());
            }
        }
        return Result.error(CommonResultCode.PARAM_ERROR);
    }
}
