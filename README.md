Semantic Propositional Image Caption Evaluation (SPICE)
===================

Evaluation code for machine-generated image captions.

## Requirements ##
- java 1.8.0+

## Dependencies ##
- Stanford [CoreNLP](http://stanfordnlp.github.io/CoreNLP/) 3.6.0
- Stanford [Scene Graph Parser](http://nlp.stanford.edu/software/scenegraph-parser.shtml)
- [Meteor](http://www.cs.cmu.edu/~alavie/METEOR/) 1.5 (for synset matching)

## Usage ##

To run SPICE, call the following (from the target directory):

    java -Xmx8G -jar spice-*.jar

Running SPICE with no arguments prints the following help message:

    SPICE version 1
    
    Usage: java -Xmx8G -jar spice-*.jar <input.json> [options]
    
    Options:
    -out <outfile>                   Output json scores and tuples data to <outfile>
    -cache <dir>                     Set directory for caching reference caption parses
    -threads <num>                   Defaults to the number of processors
    -detailed                        Include propositions for each caption in json output.
    -noSynsets                       Disable METEOR-based synonym matching
    -subset                          Report results in <outfile> for various semantic tuple subsets
    -silent                          Disable stdout results
    
    See README file for additional information and input format details

The input.json file should contain of an array of json objects, each representing a single caption and containing `image_id`, `test` and `refs` fields. See `example_input.json`

It is recommended to provide a path to an empty directory in the `-cache` argument as it makes repeated evaluations much faster.

## Build ##
To build SPICE and its dependencies from source, and run tests, use Maven with the following command: `mvn clean verify`. The jar file spice-*.jar will be created in the target directory, with required dependencies in target/src.

Building SPICE from source is NOT required as precompiled jar files are available on the [project page](http://panderson.me/spice).

## A note on the magnitude of SPICE scores ## 
On MS COCO, with 5 reference captions scores are typically in the range 0.15 - 0.20. With 40 reference captions, scores are typically in the range 0.03 - 0.07. This is the expected result due to the impact of the recall component of the metric. To make the scores more readable, on the [MS COCO leaderboard](http://mscoco.org/dataset/#captions-leaderboard), C40 SPICE scores are multiplied by 10.

## Policy gradient optimization of SPICE ##
We read with interest a [paper](https://arxiv.org/abs/1612.00370) that directly optimized SPICE (and other metrics) using policy gradients. The results indicated that optimizing SPICE and CIDEr (SPIDEr) produced the best captions, but that optimizing SPICE on its own leads to ungrammatical results. This is because SPICE ignores, and does not penalize repeated scene graph tuples. However, it would be straightforward to adjust the metric to penalize repetition. Contact us for details.

## References ##
If you report SPICE scores, please cite the SPICE paper:
- [Semantic Propositional Image Caption Evaluation (SPICE)](http://panderson.me/images/SPICE.pdf) 
- [bibtex](http://panderson.me/images/SPICE.bib)

## Developers ##
- [Peter Anderson](http://panderson.me) (Australian National University) (peter.anderson@anu.edu.au)

## Acknowledgements ##
- This work is based on the [SceneGraphParser](http://nlp.stanford.edu/software/scenegraph-parser.shtml) developed by [Sebastian Schuster](http://sebschu.com/) (Stanford).
- We re-use the Wordnet synset matching code from [Meteor 1.5](http://www.cs.cmu.edu/~alavie/METEOR/) to identify synonyms.

## License ##
- GNU AGPL v3
