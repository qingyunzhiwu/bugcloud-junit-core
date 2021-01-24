package bugcloud.junit.core.util;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class JavassistUtils {
	/**
	 * 对象转MemberObj
	 * @param obj
	 * @param cp
	 * @return
	 */
	public static MemberValue obj2MemberObj(Object obj, ConstPool cp) {
		if (obj instanceof String) {
			return new StringMemberValue((String) obj, cp);
		} else if (obj instanceof Integer) {
			return new IntegerMemberValue((Integer) obj, cp);
		} else if (obj instanceof Integer) {
			return new IntegerMemberValue((Integer) obj, cp);
		} else if (obj instanceof Boolean) {
			return new BooleanMemberValue((Boolean) obj, cp);
		} else if (obj instanceof Byte) {
			return new ByteMemberValue((Byte) obj, cp);
		} else if (obj instanceof Character) {
			return new CharMemberValue((Character) obj, cp);
		} else if (obj instanceof Class) {
			String className = ((Class<?>) obj).getName();
			return new ClassMemberValue(className, cp);
		} else if (obj instanceof Double) {
			return new DoubleMemberValue((Double) obj, cp);
		} else if (obj instanceof Enum) {
			EnumMemberValue emv = new EnumMemberValue(cp);
			emv.setType(obj.getClass().getName());
			emv.setValue(obj.toString());
			return emv;
		} else if (obj instanceof Float) {
			return new FloatMemberValue((Float) obj, cp);
		} else if (obj instanceof Long) {
			return new LongMemberValue((Long) obj, cp);
		} else if (obj instanceof Short) {
			return new ShortMemberValue((Short) obj, cp);
		} else {
			return null;
		}
	}
}
