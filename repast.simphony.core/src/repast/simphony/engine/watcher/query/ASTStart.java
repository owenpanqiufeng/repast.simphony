/* Generated By:JJTree: Do not edit this line. ASTStart.java */

package repast.simphony.engine.watcher.query;

import repast.simphony.context.Context;

public class ASTStart extends SimpleNode {
  public ASTStart(int id) {
    super(id);
  }

  public ASTStart(QueryParser p, int id) {
    super(p, id);
  }

	public IBooleanExpression buildExpression(Context context) {
		return 	((BooleanExpressionBuilder)children[0]).buildExpression(context);
	}
}
