// Example of how to write an LLVM pass
// For more information see: http://llvm.org/docs/WritingAnLLVMPass.html



#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/Transforms/Utils/Local.h"
#include "llvm/IR/InstIterator.h"
#include "llvm/IR/InstrTypes.h"

#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Transforms/IPO/PassManagerBuilder.h"
#include <vector>
using namespace llvm;


namespace {
struct MyPass : public FunctionPass {
  static char ID;
  MyPass() : FunctionPass(ID) {}

  bool runOnFunction(Function &F) override {
    SmallVector<Instruction*, 64> worklist;
    bool changed = false;
    do {
      changed = false;

      for (inst_iterator I = inst_begin(F), E=inst_end(F); I!=E;++I) {
        Instruction *i = &*I;
        if (llvm::isInstructionTriviallyDead(i)) {
          errs() << "Dead" << i << "\n";
          worklist.push_back(i);
        } else {
          errs() << "Alive" << i << "\n";
        }
      }

      while(!worklist.empty()){
        Instruction * I = worklist.pop_back_val();
        errs() << "Remove" << I << "\n";
        I->eraseFromParent();
        changed = true;
      }
    } while (changed);
    return false;
  }
  
};
}

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My simple dead code elimination pass");

static RegisterStandardPasses Y(
    PassManagerBuilder::EP_EarlyAsPossible,
    [](const PassManagerBuilder &Builder,
       legacy::PassManagerBase &PM) { PM.add(new MyPass()); });

