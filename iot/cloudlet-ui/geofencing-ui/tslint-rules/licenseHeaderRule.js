/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function Rule() {
  Lint.Rules.AbstractRule.apply(this, arguments);
}

Rule.prototype = Object.create(Lint.Rules.AbstractRule.prototype);
Rule.prototype.apply = function(sourceFile) {
  return this.applyWithWalker(new LicenseHeaderWalker(sourceFile, this.getOptions()));
};

function LicenseHeaderWalker() {
  Lint.RuleWalker.apply(this, arguments);
}

LicenseHeaderWalker.prototype = Object.create(Lint.RuleWalker.prototype);
LicenseHeaderWalker.prototype.visitSourceFile = function (node) {
  // create a failure at the current position
  var sourceText = this.getSourceFile().text;

  var licenceHeader = this.getOptions()[0];

  if ( sourceText.indexOf(licenceHeader) !== 0 ){
    this.addFailure(this.createFailure(0, 0, "Missing or incorrect project license header."));
  }

  Lint.RuleWalker.prototype.visitSourceFile.call(this, node);
};

exports.Rule = Rule;
