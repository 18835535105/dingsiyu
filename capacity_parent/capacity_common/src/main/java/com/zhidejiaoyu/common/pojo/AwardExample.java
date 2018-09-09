package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AwardExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AwardExample() {
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

        public Criteria andStudentIdIsNull() {
            addCriterion("student_id is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNotNull() {
            addCriterion("student_id is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdEqualTo(Long value) {
            addCriterion("student_id =", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotEqualTo(Long value) {
            addCriterion("student_id <>", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThan(Long value) {
            addCriterion("student_id >", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id >=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThan(Long value) {
            addCriterion("student_id <", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThanOrEqualTo(Long value) {
            addCriterion("student_id <=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdIn(List<Long> values) {
            addCriterion("student_id in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotIn(List<Long> values) {
            addCriterion("student_id not in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdBetween(Long value1, Long value2) {
            addCriterion("student_id between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotBetween(Long value1, Long value2) {
            addCriterion("student_id not between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andMedalTypeIsNull() {
            addCriterion("medal_type is null");
            return (Criteria) this;
        }

        public Criteria andMedalTypeIsNotNull() {
            addCriterion("medal_type is not null");
            return (Criteria) this;
        }

        public Criteria andMedalTypeEqualTo(Long value) {
            addCriterion("medal_type =", value, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeNotEqualTo(Long value) {
            addCriterion("medal_type <>", value, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeGreaterThan(Long value) {
            addCriterion("medal_type >", value, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeGreaterThanOrEqualTo(Long value) {
            addCriterion("medal_type >=", value, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeLessThan(Long value) {
            addCriterion("medal_type <", value, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeLessThanOrEqualTo(Long value) {
            addCriterion("medal_type <=", value, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeIn(List<Long> values) {
            addCriterion("medal_type in", values, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeNotIn(List<Long> values) {
            addCriterion("medal_type not in", values, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeBetween(Long value1, Long value2) {
            addCriterion("medal_type between", value1, value2, "medalType");
            return (Criteria) this;
        }

        public Criteria andMedalTypeNotBetween(Long value1, Long value2) {
            addCriterion("medal_type not between", value1, value2, "medalType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeIsNull() {
            addCriterion("award_content_type is null");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeIsNotNull() {
            addCriterion("award_content_type is not null");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeEqualTo(Integer value) {
            addCriterion("award_content_type =", value, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeNotEqualTo(Integer value) {
            addCriterion("award_content_type <>", value, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeGreaterThan(Integer value) {
            addCriterion("award_content_type >", value, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("award_content_type >=", value, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeLessThan(Integer value) {
            addCriterion("award_content_type <", value, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeLessThanOrEqualTo(Integer value) {
            addCriterion("award_content_type <=", value, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeIn(List<Integer> values) {
            addCriterion("award_content_type in", values, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeNotIn(List<Integer> values) {
            addCriterion("award_content_type not in", values, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeBetween(Integer value1, Integer value2) {
            addCriterion("award_content_type between", value1, value2, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andAwardContentTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("award_content_type not between", value1, value2, "awardContentType");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Integer value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Integer value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Integer value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Integer value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Integer value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Integer> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Integer> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Integer value1, Integer value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andCanGetIsNull() {
            addCriterion("can_get is null");
            return (Criteria) this;
        }

        public Criteria andCanGetIsNotNull() {
            addCriterion("can_get is not null");
            return (Criteria) this;
        }

        public Criteria andCanGetEqualTo(Integer value) {
            addCriterion("can_get =", value, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetNotEqualTo(Integer value) {
            addCriterion("can_get <>", value, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetGreaterThan(Integer value) {
            addCriterion("can_get >", value, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetGreaterThanOrEqualTo(Integer value) {
            addCriterion("can_get >=", value, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetLessThan(Integer value) {
            addCriterion("can_get <", value, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetLessThanOrEqualTo(Integer value) {
            addCriterion("can_get <=", value, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetIn(List<Integer> values) {
            addCriterion("can_get in", values, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetNotIn(List<Integer> values) {
            addCriterion("can_get not in", values, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetBetween(Integer value1, Integer value2) {
            addCriterion("can_get between", value1, value2, "canGet");
            return (Criteria) this;
        }

        public Criteria andCanGetNotBetween(Integer value1, Integer value2) {
            addCriterion("can_get not between", value1, value2, "canGet");
            return (Criteria) this;
        }

        public Criteria andGetFlagIsNull() {
            addCriterion("get_flag is null");
            return (Criteria) this;
        }

        public Criteria andGetFlagIsNotNull() {
            addCriterion("get_flag is not null");
            return (Criteria) this;
        }

        public Criteria andGetFlagEqualTo(Integer value) {
            addCriterion("get_flag =", value, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagNotEqualTo(Integer value) {
            addCriterion("get_flag <>", value, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagGreaterThan(Integer value) {
            addCriterion("get_flag >", value, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("get_flag >=", value, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagLessThan(Integer value) {
            addCriterion("get_flag <", value, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagLessThanOrEqualTo(Integer value) {
            addCriterion("get_flag <=", value, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagIn(List<Integer> values) {
            addCriterion("get_flag in", values, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagNotIn(List<Integer> values) {
            addCriterion("get_flag not in", values, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagBetween(Integer value1, Integer value2) {
            addCriterion("get_flag between", value1, value2, "getFlag");
            return (Criteria) this;
        }

        public Criteria andGetFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("get_flag not between", value1, value2, "getFlag");
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

        public Criteria andGetTimeIsNull() {
            addCriterion("get_time is null");
            return (Criteria) this;
        }

        public Criteria andGetTimeIsNotNull() {
            addCriterion("get_time is not null");
            return (Criteria) this;
        }

        public Criteria andGetTimeEqualTo(Date value) {
            addCriterion("get_time =", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotEqualTo(Date value) {
            addCriterion("get_time <>", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeGreaterThan(Date value) {
            addCriterion("get_time >", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("get_time >=", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeLessThan(Date value) {
            addCriterion("get_time <", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeLessThanOrEqualTo(Date value) {
            addCriterion("get_time <=", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeIn(List<Date> values) {
            addCriterion("get_time in", values, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotIn(List<Date> values) {
            addCriterion("get_time not in", values, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeBetween(Date value1, Date value2) {
            addCriterion("get_time between", value1, value2, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotBetween(Date value1, Date value2) {
            addCriterion("get_time not between", value1, value2, "getTime");
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