package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PayCardExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PayCardExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andOrderNoIsNull() {
            addCriterion("order_no is null");
            return (Criteria) this;
        }

        public Criteria andOrderNoIsNotNull() {
            addCriterion("order_no is not null");
            return (Criteria) this;
        }

        public Criteria andOrderNoEqualTo(String value) {
            addCriterion("order_no =", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotEqualTo(String value) {
            addCriterion("order_no <>", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoGreaterThan(String value) {
            addCriterion("order_no >", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoGreaterThanOrEqualTo(String value) {
            addCriterion("order_no >=", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoLessThan(String value) {
            addCriterion("order_no <", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoLessThanOrEqualTo(String value) {
            addCriterion("order_no <=", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoLike(String value) {
            addCriterion("order_no like", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotLike(String value) {
            addCriterion("order_no not like", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoIn(List<String> values) {
            addCriterion("order_no in", values, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotIn(List<String> values) {
            addCriterion("order_no not in", values, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoBetween(String value1, String value2) {
            addCriterion("order_no between", value1, value2, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotBetween(String value1, String value2) {
            addCriterion("order_no not between", value1, value2, "orderNo");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNull() {
            addCriterion("card_no is null");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNotNull() {
            addCriterion("card_no is not null");
            return (Criteria) this;
        }

        public Criteria andCardNoEqualTo(String value) {
            addCriterion("card_no =", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotEqualTo(String value) {
            addCriterion("card_no <>", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThan(String value) {
            addCriterion("card_no >", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThanOrEqualTo(String value) {
            addCriterion("card_no >=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThan(String value) {
            addCriterion("card_no <", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThanOrEqualTo(String value) {
            addCriterion("card_no <=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLike(String value) {
            addCriterion("card_no like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotLike(String value) {
            addCriterion("card_no not like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoIn(List<String> values) {
            addCriterion("card_no in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotIn(List<String> values) {
            addCriterion("card_no not in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoBetween(String value1, String value2) {
            addCriterion("card_no between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotBetween(String value1, String value2) {
            addCriterion("card_no not between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andValidityTimeIsNull() {
            addCriterion("validity_time is null");
            return (Criteria) this;
        }

        public Criteria andValidityTimeIsNotNull() {
            addCriterion("validity_time is not null");
            return (Criteria) this;
        }

        public Criteria andValidityTimeEqualTo(Integer value) {
            addCriterion("validity_time =", value, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeNotEqualTo(Integer value) {
            addCriterion("validity_time <>", value, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeGreaterThan(Integer value) {
            addCriterion("validity_time >", value, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("validity_time >=", value, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeLessThan(Integer value) {
            addCriterion("validity_time <", value, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeLessThanOrEqualTo(Integer value) {
            addCriterion("validity_time <=", value, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeIn(List<Integer> values) {
            addCriterion("validity_time in", values, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeNotIn(List<Integer> values) {
            addCriterion("validity_time not in", values, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeBetween(Integer value1, Integer value2) {
            addCriterion("validity_time between", value1, value2, "validityTime");
            return (Criteria) this;
        }

        public Criteria andValidityTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("validity_time not between", value1, value2, "validityTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeIsNull() {
            addCriterion("use_time is null");
            return (Criteria) this;
        }

        public Criteria andUseTimeIsNotNull() {
            addCriterion("use_time is not null");
            return (Criteria) this;
        }

        public Criteria andUseTimeEqualTo(Date value) {
            addCriterion("use_time =", value, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeNotEqualTo(Date value) {
            addCriterion("use_time <>", value, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeGreaterThan(Date value) {
            addCriterion("use_time >", value, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("use_time >=", value, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeLessThan(Date value) {
            addCriterion("use_time <", value, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeLessThanOrEqualTo(Date value) {
            addCriterion("use_time <=", value, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeIn(List<Date> values) {
            addCriterion("use_time in", values, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeNotIn(List<Date> values) {
            addCriterion("use_time not in", values, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeBetween(Date value1, Date value2) {
            addCriterion("use_time between", value1, value2, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseTimeNotBetween(Date value1, Date value2) {
            addCriterion("use_time not between", value1, value2, "useTime");
            return (Criteria) this;
        }

        public Criteria andUseUserIsNull() {
            addCriterion("use_user is null");
            return (Criteria) this;
        }

        public Criteria andUseUserIsNotNull() {
            addCriterion("use_user is not null");
            return (Criteria) this;
        }

        public Criteria andUseUserEqualTo(String value) {
            addCriterion("use_user =", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserNotEqualTo(String value) {
            addCriterion("use_user <>", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserGreaterThan(String value) {
            addCriterion("use_user >", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserGreaterThanOrEqualTo(String value) {
            addCriterion("use_user >=", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserLessThan(String value) {
            addCriterion("use_user <", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserLessThanOrEqualTo(String value) {
            addCriterion("use_user <=", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserLike(String value) {
            addCriterion("use_user like", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserNotLike(String value) {
            addCriterion("use_user not like", value, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserIn(List<String> values) {
            addCriterion("use_user in", values, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserNotIn(List<String> values) {
            addCriterion("use_user not in", values, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserBetween(String value1, String value2) {
            addCriterion("use_user between", value1, value2, "useUser");
            return (Criteria) this;
        }

        public Criteria andUseUserNotBetween(String value1, String value2) {
            addCriterion("use_user not between", value1, value2, "useUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(String value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(String value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(String value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(String value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(String value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(String value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLike(String value) {
            addCriterion("create_user like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotLike(String value) {
            addCriterion("create_user not like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<String> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<String> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(String value1, String value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(String value1, String value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserIsNull() {
            addCriterion("goods_user is null");
            return (Criteria) this;
        }

        public Criteria andGoodsUserIsNotNull() {
            addCriterion("goods_user is not null");
            return (Criteria) this;
        }

        public Criteria andGoodsUserEqualTo(String value) {
            addCriterion("goods_user =", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserNotEqualTo(String value) {
            addCriterion("goods_user <>", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserGreaterThan(String value) {
            addCriterion("goods_user >", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserGreaterThanOrEqualTo(String value) {
            addCriterion("goods_user >=", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserLessThan(String value) {
            addCriterion("goods_user <", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserLessThanOrEqualTo(String value) {
            addCriterion("goods_user <=", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserLike(String value) {
            addCriterion("goods_user like", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserNotLike(String value) {
            addCriterion("goods_user not like", value, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserIn(List<String> values) {
            addCriterion("goods_user in", values, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserNotIn(List<String> values) {
            addCriterion("goods_user not in", values, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserBetween(String value1, String value2) {
            addCriterion("goods_user between", value1, value2, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andGoodsUserNotBetween(String value1, String value2) {
            addCriterion("goods_user not between", value1, value2, "goodsUser");
            return (Criteria) this;
        }

        public Criteria andCardNumIsNull() {
            addCriterion("card_num is null");
            return (Criteria) this;
        }

        public Criteria andCardNumIsNotNull() {
            addCriterion("card_num is not null");
            return (Criteria) this;
        }

        public Criteria andCardNumEqualTo(Integer value) {
            addCriterion("card_num =", value, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumNotEqualTo(Integer value) {
            addCriterion("card_num <>", value, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumGreaterThan(Integer value) {
            addCriterion("card_num >", value, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("card_num >=", value, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumLessThan(Integer value) {
            addCriterion("card_num <", value, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumLessThanOrEqualTo(Integer value) {
            addCriterion("card_num <=", value, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumIn(List<Integer> values) {
            addCriterion("card_num in", values, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumNotIn(List<Integer> values) {
            addCriterion("card_num not in", values, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumBetween(Integer value1, Integer value2) {
            addCriterion("card_num between", value1, value2, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCardNumNotBetween(Integer value1, Integer value2) {
            addCriterion("card_num not between", value1, value2, "cardNum");
            return (Criteria) this;
        }

        public Criteria andCanUseIsNull() {
            addCriterion("can_use is null");
            return (Criteria) this;
        }

        public Criteria andCanUseIsNotNull() {
            addCriterion("can_use is not null");
            return (Criteria) this;
        }

        public Criteria andCanUseEqualTo(Integer value) {
            addCriterion("can_use =", value, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseNotEqualTo(Integer value) {
            addCriterion("can_use <>", value, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseGreaterThan(Integer value) {
            addCriterion("can_use >", value, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseGreaterThanOrEqualTo(Integer value) {
            addCriterion("can_use >=", value, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseLessThan(Integer value) {
            addCriterion("can_use <", value, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseLessThanOrEqualTo(Integer value) {
            addCriterion("can_use <=", value, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseIn(List<Integer> values) {
            addCriterion("can_use in", values, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseNotIn(List<Integer> values) {
            addCriterion("can_use not in", values, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseBetween(Integer value1, Integer value2) {
            addCriterion("can_use between", value1, value2, "canUse");
            return (Criteria) this;
        }

        public Criteria andCanUseNotBetween(Integer value1, Integer value2) {
            addCriterion("can_use not between", value1, value2, "canUse");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}