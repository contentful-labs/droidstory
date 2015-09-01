#!/usr/bin/env node

var pkg = require('./package.json');
var program = require('commander');

program.version(pkg.version)
    .usage('<src_space_id> <src_cda_token> <cma_token>')
    .option('-o, --org-id <id>', 'Target organization ID')
    .parse(process.argv);

if (program.args.length < 1) {
  program.help();
}

var cda = require('contentful');
var cma = require('contentful-management');
var ContentSync = require('contentful-publication/lib/ContentSync.js');

var srcSpaceId = program.args[0];
var srcDeliveryToken = program.args[1];
var cmaToken = program.args[2];
var orgId = program.orgId;
var cdaClient = cda.createClient({space: srcSpaceId, accessToken: srcDeliveryToken});
var cmaClient = cma.createClient({accessToken: cmaToken});

cdaClient.space().then(function (srcSpace) {
  return cmaClient.createSpace({name: srcSpace.name}, orgId)
      .then(function (space) {
        return space.createContentType({
          sys: {id: '5RFNQmUj5Y6aAggqAswiqK'},
          name: 'Story',
          fields: [
            {id: 'codeName', name: 'Code Name', type: 'Symbol', required: true},
            {id: 'version', name: 'Version', type: 'Number', required: true},
            {id: 'releaseDate', name: 'Release Date', type: 'Date', required: true},
            {id: 'apiLevel', name: 'API Level', type: 'Integer', required: true},
            {id: 'teaser', name: 'Teaser', type: 'Text', required: true},
            {id: 'images', name: 'Images', type: 'Array', required: true, items: {
              type: 'Link',
              linkType: 'Asset'
            }}
          ]
        }).then(function (res) {
          return space.publishContentType(res);
        }).then(function (res) {
          return ContentSync.fromConfig({
            tokenFilename: 'tmp',
            contentful: {
              sourceSpace: srcSpaceId,
              sourceContentDeliveryToken: srcDeliveryToken,
              destinationSpace: space.sys.id,
              contentManagementAccessToken: cmaToken,
              publishRetryDelay: 30000
            }
          }).run()
        }).then(function (res) {
          return cmaClient.request('/spaces/' + space.sys.id + '/api_keys', {
            method: 'POST',
            body: JSON.stringify({
              name: 'DroidSpace tutorial key',
              description: 'Created by the droidstory-spacecreator tool. https://github.com/contentful-labs/droidstory'
            });
          });
        }).then(function (apiKey) {
          return {
            spaceId: space.sys.id,
            accessToken: apiKey.accessToken
          };
        });
      });
}).catch(function (error) {
  console.log('Failure: ', error);
  throw error;
}).then(function (result) {
  console.log('All done');
  console.log('Space ID:', result.spaceId);
  console.log('Access Token:', result.accessToken);
});
