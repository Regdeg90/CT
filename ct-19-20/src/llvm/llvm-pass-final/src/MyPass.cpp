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
#include <unordered_map>
#include <utility>
#include <set>
using namespace llvm;

namespace
{
struct MyPass : public FunctionPass
{
  static char ID;
  MyPass() : FunctionPass(ID) {}
  std::set<Value *> out;
  std::unordered_map<Instruction *, std::pair<std::set<Value *>, std::set<Value *>>> sets;
  std::unordered_map<Instruction *, std::pair<std::set<Value *>, std::set<Value *>>> previoussets;
  std::unordered_map<Instruction *, std::set<Instruction *>> successors;
  SmallVector<Instruction *, 64> worklist;
  // Instruction *previnst = NULL;

  bool differnce(std::unordered_map<Instruction *, std::pair<std::set<Value *>, std::set<Value *>>> sets, std::unordered_map<Instruction *, std::pair<std::set<Value *>, std::set<Value *>>> previoussets)
  {
    std::unordered_map<Instruction *, std::pair<std::set<Value *>, std::set<Value *>>>::iterator it;

    for (it = sets.begin(); it != sets.end(); it++)
    {

      if (previoussets.find(it->first) == previoussets.end())
      {
        return false;
      }
      std::set<Value *> a = it->second.first;
      std::set<Value *> b = it->second.second;

      std::set<Value *> prea = previoussets.at(it->first).first;
      std::set<Value *> preb = previoussets.at(it->first).second;

      std::set<Value *>::iterator i;
      for (i = a.begin(); i != a.end(); i++)
      {
        if (prea.find(*i) == prea.end())
        {
          return false;
        }
      }

      for (i = b.begin(); i != b.end(); i++)
      {
        if (preb.find(*i) == preb.end())
        {
          return false;
        }
      }
    }
    // errs() << "true";
    return true;
  };

  void printLiveness(Function &F)
  {
    errs() << "\n\n\n";
    for (Function::iterator bb = F.begin(), end = F.end(); bb != end; bb++)
    {
      for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; i++)
      {
        // skip phis
        if (dyn_cast<PHINode>(i))
          continue;

        errs() << "{";
        std::pair<std::set<Value *>, std::set<Value *>> currentset;
        Instruction *p = &*i;
        auto operatorSet = sets.at(p).first;
        for (auto oper = operatorSet.begin(); oper != operatorSet.end(); oper++)
        {
          auto op = *oper;
          if (oper != operatorSet.begin())
            errs() << ", ";
          (*oper)->printAsOperand(errs(), false);
        }

        errs() << "}\n";
        errs() << "Instruction : " << *i << "\n";
      }
    }
    errs() << "{}";
  };

  void printsets(std::unordered_map<Instruction *, std::pair<std::set<Value *>, std::set<Value *>>> sets, std::unordered_map<Instruction *, std::set<Instruction *>> successors)
  {
    std::unordered_map<Instruction *, std::pair<std::set<Value *>, std::set<Value *>>>::iterator it;

    for (it = sets.begin(); it != sets.end(); it++)
    {
      errs() << "-----------------------------\n";
      errs() << "Instruction: " << *it->first << "\n";

      errs() << "In Set: ";
      std::set<Value *>::iterator i;
      for (i = it->second.first.begin(); i != it->second.first.end(); i++)
      {

        (*i)->printAsOperand(errs(), false);

        errs() << " ";
      }
      errs() << "\n";

      errs() << "Out Set: ";
      for (i = it->second.second.begin(); i != it->second.second.end(); i++)
      {

        (*i)->printAsOperand(errs(), false);

        errs() << " ";
      }
      errs() << "\n";

      if (successors.find(it->first) != successors.end())
      {
        errs() << "Successor: ";
        std::set<Instruction *>::iterator j;
        for (j = successors.at(it->first).begin(); j != successors.at(it->first).end(); j++)
        {

          (*j)->printAsOperand(errs(), false);

          errs() << " ";
        }

        errs() << "\n";
      }

      errs() << "\n\n";
    }
  };

  void setsuccessor(Instruction *inst, Instruction *previnst)
  {
    if (previnst != NULL && isa<BranchInst>(previnst))
    {
      // errs() << "it has " << previnst->getNumSuccessors() << " and succ(0) " << *(previnst->getSuccessor(0)) << "\n";
      for (int t = 0; t < previnst->getNumSuccessors(); t++)
      {
        Instruction *p = &*(previnst->getSuccessor(t)->begin());
        if (successors.find(previnst) != successors.end())
        {
          // errs() << "\n\n using front: " ;
          // errs() << "Added to succsor: " << *(p) << " to " << *previnst << "\n";

          // Instruction * tempinstruct = previnst->getSuccessor(t).begi;
          successors.at(previnst).insert(p);
        }
        else
        {
          // errs() << "Added to succsor: " << *(p) << " to " << *previnst << "\n";
          std::set<Instruction *> temp;
          temp.insert(p);

          successors.insert({previnst, temp});
        }
      }

      errs() << "its a branch \n";
    }
    // else if (previnst != NULL && isa<PHINode>(prvinst)) {

    // }
    else
    {
      if (previnst != NULL && successors.find(previnst) != successors.end())
      {
        // errs() << "here i am";
        // errs() << "Added to succsor: " << *inst << " to " << *previnst << "\n";
        successors.at(previnst).insert(inst);
      }
      else if (previnst != NULL)
      {
        // errs() << "Added to succsor: " << *inst << " to " << *previnst << " \n";
        std::set<Instruction *> temp;
        temp.insert(inst);
        successors.insert({previnst, temp});
      }
    }

    // return successors;
  };
  std::pair<std::set<Value *>, std::set<Value *>> getsets(Instruction *inst, std::pair<std::set<Value *>, std::set<Value *>> currentsets)
  {
    if (sets.find(inst) == sets.end())
    {
      // errs() <<"not found\n";
      currentsets.first = {};
      currentsets.second = {};
    }
    else
    {
      // errs() << "found\n";
      currentsets.first = sets.at(inst).first;
      currentsets.second = sets.at(inst).second;

      // currentsets = sets.at(inst);
    }
    return currentsets;
  };

  std::set<Value *> getinset(std::pair<std::set<Value *>, std::set<Value *>> currentsets)
  {

    for (auto &i : currentsets.second)
    {
      if (currentsets.first.find(i) == currentsets.first.end())
      {
        currentsets.first.insert(i);
      }
    }

    return currentsets.first;
  };

  std::set<Value *> getoutset(std::pair<std::set<Value *>, std::set<Value *>> currentsets, Instruction *inst)
  {
    if (successors.find(inst) == successors.end())
    {
      currentsets.second = {};
    }
    else
    {
      currentsets.second = {};

      std::set<Instruction *>::iterator it = successors.at(inst).begin();
      while (it != successors.at(inst).end())
      {
        // Instruction *tempinstrct = *(successors.at(inst).getInstruction(i));
        if (sets.find(*it) != sets.end())
        {
          std::set<Value *> temp = sets.at(*it).first;

          currentsets.second.insert(temp.begin(), temp.end());

          if (isa<PHINode>(*it))
          {
            PHINode *tempinst = cast<PHINode>(*it);
            BasicBlock *bb = inst->getParent();

            // for (int i = 0; i < tempinst->getNumIncomingValues(); i++)
            // {
            //   if (tempinst->getIncomingBlock(i) != bb)
            //   {

            //     // if (currentsets.second.find(tempinst->getIncomingBlock(i)) != currentsets.second.end())
            //     // {
            //     for (Value *V : currentsets.second)
            //     {
            //       errs() << "Instruction: " << *V << "\n";
            //     }
            //     errs() << "\n\n\n\n\n\n removing \n\n\n\n\n\n";
            //     errs() << "Block: " << *bb->begin() << "\n";
            //     errs() << "Doesn't get this block: " << *tempinst->getIncomingBlock(i)->begin() << "\n";
            //     BasicBlock *bb2 = tempinst->getIncomingBlock(i);
            //     if(Value* val = dyn_cast<Value>(tempinst->getIncomingValue(i))){
            //       currentsets.second.erase(val);

            //     }
                

            //     for (Value *V : currentsets.second)
            //     {
            //       errs() << "Instruction: " << *V << "\n";
            //     }

            //     // }
            //   }
            // }
          }
        }

        it++;
      }
    }
    return currentsets.second;
  };

  bool runOnFunction(Function &F) override
  {

    bool changed = false;
    do
    {
      changed = false;
      out.clear();
      sets.clear();
      previoussets.clear();
      successors.clear();
      worklist.clear();
      Instruction *previnst = NULL;
      do
      {
        previoussets = sets;
        for (Function::iterator i = F.begin(), iend = F.end(); i != iend; ++i)
        {
          for (BasicBlock::iterator j = i->begin(), jend = i->end(); j != jend; ++j)
          {

            Instruction *inst = &*j;

            setsuccessor(inst, previnst);

            std::pair<std::set<Value *>, std::set<Value *>> currentsets;
            currentsets = getsets(inst, currentsets);

            // errs() << "somehwer here";

            //Use set
            if (false)
            {
              PHINode *tempinst = cast<PHINode>(inst);

              BasicBlock *bb = inst->getParent();

              errs() << "It's a phi node: " << *inst << "\n";

              errs() << "Parent block start is: " << *bb->getFirstNonPHI() << "\n";

              // std::unordered_map<Instruction *, std::set<Instruction *>>::iterator it = successors.begin();

              // // std::set<Instruction *>::iterator it = successors.at(inst).begin();

              // // if (successors.find(inst) != successors.end())
              // // {

              // while (it != successors.end())
              // {

              // }
              // }
            }
            else
            {
              errs() << "It's an instruction: " << *(inst) << "\n";
              for (int t = 0; t < inst->getNumOperands(); t++)
              {
                if (dyn_cast<Instruction>(inst->getOperand(t)) || dyn_cast<Argument>(inst->getOperand(t)))
                // if (!(inst->isTerminator()))
                {

                  errs() << "It's an oprand: " << *(inst->getOperand(t)) << "\n";
                  currentsets.first.insert(inst->getOperand(t));
                }
              }
            }

            currentsets.second.erase(inst);
            // if (isa<PHINode>(inst))
            // {
            //   PHINode *tempinst = cast<PHINode>(inst);
            //   if(tempinst->getParent() != previnst->getParent()) {
            //     int i = tempinst->getBasicBlockIndex(previnst->getParent());

            //   }

            // }
            // else
            // {
            currentsets.first = getinset(currentsets);
            // }

            currentsets.second = getoutset(currentsets, inst);

            if (sets.find(inst) == sets.end())
            {
              sets.insert({inst, currentsets});
            }
            else
            {
              sets.at(inst) = currentsets;
            }
            previnst = inst;
          }
        }
        previnst = NULL;
        errs() << "-------------------- New Loop ----------\n";
        printsets(sets, successors);
      } while (!differnce(sets, previoussets));

      for (Function::iterator i = F.begin(), iend = F.end(); i != iend; ++i)
      {
        for (BasicBlock::reverse_iterator j = i->rbegin(), jend = i->rend(); j != jend; ++j)
        {
          Instruction *p = &*j;
          std::pair<std::set<Value *>, std::set<Value *>> currentset;
          currentset = sets.at(p);

          if (!(p->mayHaveSideEffects() || p->isTerminator()))
          {
            if (currentset.second.find(p) == currentset.second.end())
            {
              worklist.push_back(p);
            }
          }
        }
      }
      while (!worklist.empty())
      {
        Instruction *I = worklist.pop_back_val();
        errs() << "Remove" << *I << "\n\n\n\n\n\n";
        I->eraseFromParent();
        changed = true;
      }
      // } while (previn!= in && prevout != out);

      // } while (changed);
      // printsets(sets, successors);
    } while (changed);

    printLiveness(F);
    return false;
  }
};
} // namespace

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My liveness analysis and dead code elimination pass");

static RegisterStandardPasses Y(
    PassManagerBuilder::EP_EarlyAsPossible,
    [](const PassManagerBuilder &Builder,
       legacy::PassManagerBase &PM) { PM.add(new MyPass()); });
